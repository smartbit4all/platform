package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.domain.utility.crud.CrudRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class SearchIndexWithFilterBean<F, O> extends SearchIndexImpl<F, O> {

  private ObjectDefinition<F> filterDefinition;

  /**
   * {@link LinkedHashMap} to preserve the parameterization order in the entity definition.
   */
  private Map<String, PropertyMapping> pathByPropertyName = new LinkedHashMap<>();

  private Map<String, CustomExpressionMapping> expressionByPropertyName = new LinkedHashMap<>();

  private String indexedObjectSchema;

  private ObjectDefinition<O> indexedObjectDefinition;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private CrudApi crudApi;

  @Autowired
  private TableDataApi tableDataApi;

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private EntityManager entityManager;

  private Class<F> filterDefinitionClass;

  private Class<O> indexedObjectDefinitionClass;

  private final class PropertyMapping {

    String[] path;

    UnaryOperator<Object> processor;

    Function<ObjectNode, Object> complexProcessor;

    public PropertyMapping(String[] path, UnaryOperator<Object> processor,
        Function<ObjectNode, Object> complexProcessor) {
      super();
      this.path = path;
      this.processor = processor;
      this.complexProcessor = complexProcessor;
    }

  }

  private final class CustomExpressionMapping {

    BiFunction<Object, Property<?>, Expression> expressionProcessor;

    BiFunction<Object, EntityDefinition, Expression> complexExpressionProcessor;

    public CustomExpressionMapping(
        BiFunction<Object, Property<?>, Expression> customExpressionProcessor,
        BiFunction<Object, EntityDefinition, Expression> complexExpressionProcessor) {
      super();
      this.expressionProcessor = customExpressionProcessor;
      this.complexExpressionProcessor = complexExpressionProcessor;
    }

  }

  public SearchIndexWithFilterBean(String logicalSchema, String name,
      Class<F> filterDefinitionClass, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass) {
    super(logicalSchema, name);
    this.filterDefinitionClass = filterDefinitionClass;
    this.indexedObjectSchema = indexedObjectSchema;
    this.indexedObjectDefinitionClass = indexedObjectDefinitionClass;
  }

  public SearchIndexWithFilterBean<F, O> map(String propertyName, String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      pathByPropertyName.put(propertyName, new PropertyMapping(pathes, null, null));
    }
    return this;
  }

  public SearchIndexWithFilterBean<F, O> map(String propertyName, UnaryOperator<Object> processor,
      String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      pathByPropertyName.put(propertyName,
          new PropertyMapping(pathes, processor, null));
    }
    return this;
  }

  public SearchIndexWithFilterBean<F, O> mapComplex(String propertyName,
      Function<ObjectNode, Object> complexProcessor) {
    Objects.requireNonNull(complexProcessor);
    pathByPropertyName.put(propertyName,
        new PropertyMapping(null, null, complexProcessor));
    return this;
  }

  public SearchIndexWithFilterBean<F, O> expression(String propertyName,
      BiFunction<Object, Property<?>, Expression> customExpression) {
    Objects.requireNonNull(customExpression);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(customExpression, null));
    return this;
  }

  public SearchIndexWithFilterBean<F, O> expressionComplex(String propertyName,
      BiFunction<Object, EntityDefinition, Expression> complexExpression) {
    Objects.requireNonNull(complexExpression);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(null, complexExpression));
    return this;
  }

  @Override
  public TableData<?> executeSearch(QueryInput queryInput) {
    queryInput.setTableDataUri(tableDataApi.save(readAllObjects()));
    return crudApi.executeQuery(queryInput).getTableData();
  }

  private final TableData<?> readAllObjects() {
    EntityDefinition def = getDefinition();
    TableData<?> tableData = new TableData<>(def);
    tableData.addColumns(def.allProperties());

    Storage storage = storageApi.get(indexedObjectSchema);
    List<URI> allObjectUris = storage.readAllUris(indexedObjectDefinition().getClazz());
    allObjectUris.stream().map(u -> objectApi.load(u))
        .forEach(n -> {
          DataRow row = tableData.addRow();
          for (DataColumn<?> col : tableData.columns()) {
            PropertyMapping mapping = pathByPropertyName.get(col.getProperty().getName());
            if (mapping.path != null && mapping.processor == null
                && mapping.complexProcessor == null) {
              tableData.setObject(col, row, n.getValue(mapping.path));
            } else if (mapping.path != null && mapping.processor != null) {
              tableData.setObject(col, row, mapping.processor.apply(n.getValue(mapping.path)));
            } else if (mapping.complexProcessor != null) {
              tableData.setObject(col, row, mapping.complexProcessor.apply(n));
            }
          }
        });

    return tableData;
  }

  @Override
  public synchronized EntityDefinition getDefinition() {
    if (definition == null) {
      definition = constructDefinition();
      entityManager.registerEntityDef(definition);
    }
    return definition;
  }

  @Override
  public EntityDefinition constructDefinition() {
    EntityDefinitionBuilder builder = EntityDefinitionBuilder.of(ctx)
        .name(name())
        .domain(logicalSchema());

    Map<String, PropertyMeta> properties = filterDefinition().meta().getProperties();

    for (String propertyName : pathByPropertyName.keySet()) {
      PropertyMeta propertyMeta = properties.get(propertyName.toUpperCase());
      if (propertyMeta != null) {
        builder.ownedProperty(propertyMeta.getName(), propertyMeta.getType());
      }
    }

    return builder.build();
  }

  private ObjectDefinition<F> filterDefinition() {
    if (filterDefinition == null) {
      filterDefinition = objectApi.definition(filterDefinitionClass);
    }
    return filterDefinition;
  }

  private ObjectDefinition<O> indexedObjectDefinition() {
    if (indexedObjectDefinition == null) {
      indexedObjectDefinition = objectApi.definition(indexedObjectDefinitionClass);
    }
    return indexedObjectDefinition;
  }

  @Override
  public TableData<?> executeSearch(F filterObject) {
    QueryInput input;
    CrudRead<EntityDefinition> read = Crud.read(getDefinition()).selectAllProperties();
    Expression exp = null;
    for (Entry<String, PropertyMeta> entry : filterDefinition().meta().getProperties().entrySet()) {
      // If the bean property is not null.
      PropertyMeta propertyMeta = entry.getValue();
      Object value = propertyMeta.getValue(filterObject);
      Expression currentExp = null;
      if (value != null) {
        Property<?> property = getDefinition().getProperty(propertyMeta.getName());
        CustomExpressionMapping customExpressionMapping =
            expressionByPropertyName.get(propertyMeta.getName());
        if (customExpressionMapping != null) {
          if (customExpressionMapping.complexExpressionProcessor != null) {
            currentExp = customExpressionMapping.complexExpressionProcessor
                .apply(value, getDefinition()).BRACKET();
          } else if (customExpressionMapping.expressionProcessor != null && property != null) {
            currentExp = customExpressionMapping.expressionProcessor.apply(value, property);
          }
        } else if (property != null) {
          currentExp = createDynamicExpression(propertyMeta.getType(), property, value);
        }
        if (exp == null) {
          exp = currentExp;
        } else if (currentExp != null) {
          exp = exp.AND(currentExp);
        }
      }
    }
    if (exp != null) {
      read.where(exp);
    }
    return executeSearch(read.getQuery());
  }

  @SuppressWarnings("unchecked")
  private Expression createDynamicExpression(Class<?> propertyType, Property<?> property,
      Object value) {
    if (propertyType.equals(String.class)) {
      return ((Property<String>) property).eq((String) value);
    } else if (propertyType.equals(Boolean.class)) {
      return ((Property<Boolean>) property).eq((Boolean) value);
    } else if (propertyType.equals(URI.class)) {
      return ((Property<URI>) property).eq((URI) value);
    } else if (propertyType.isEnum()) {
      return ((Property<Enum<?>>) property).eq((Enum<?>) value);
    } else {
      return null;
    }
  }
}
