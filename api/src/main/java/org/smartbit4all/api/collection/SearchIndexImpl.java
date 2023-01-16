package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.SearchEntityDefinition.DetailDefinition;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.query.QueryInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author Peter Boros
 *
 * @param <O>
 */
/**
 * @author Peter Boros
 *
 * @param <O>
 */
public abstract class SearchIndexImpl<O> implements SearchIndex<O> {

  private static final Logger log = LoggerFactory.getLogger(SearchIndexImpl.class);

  private String logicalSchema;

  private String name;

  private Class<O> indexedObjectDefinitionClass;

  private interface Mapping {

  }

  /**
   * The mapping of the property on a path.
   * 
   * @author Peter Boros
   */
  private final class PropertyMapping implements Mapping {

    String[] path;

    UnaryOperator<Object> processor;

    Function<ObjectNode, Object> complexProcessor;

    protected PropertyMapping(String[] path, UnaryOperator<Object> processor,
        Function<ObjectNode, Object> complexProcessor) {
      super();
      this.path = path;
      this.processor = processor;
      this.complexProcessor = complexProcessor;
    }

  }

  protected final class DetailMapping implements Mapping {

    /**
     * The path of the detail property that contains a list / map, referring to another object.
     */
    String[] path;

    /**
     * {@link LinkedHashMap} to preserve the parameterization order in the entity definition.
     */
    protected Map<String, PropertyMapping> pathByPropertyName = new LinkedHashMap<>();

  }

  protected final class CustomExpressionMapping {

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

  /**
   * {@link LinkedHashMap} to preserve the parameterization order in the entity definition.
   */
  protected Map<String, PropertyMapping> mappingByPropertyName = new LinkedHashMap<>();

  /**
   * If we need a special mapping between the filter field of a bean the this map contains the
   * lambda for this.
   */
  protected Map<String, CustomExpressionMapping> expressionByPropertyName = new LinkedHashMap<>();

  protected String indexedObjectSchema;

  protected ObjectDefinition<O> indexedObjectDefinition;

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected StorageApi storageApi;

  @Autowired
  protected CrudApi crudApi;

  @Autowired
  protected TableDataApi tableDataApi;

  @Autowired
  protected ApplicationContext ctx;

  @Autowired
  protected EntityManager entityManager;

  /**
   * The entity definition the search working on.
   */
  protected SearchEntityDefinition definition;

  @Override
  public TableData<?> executeSearch(QueryInput queryInput) {
    queryInput.setTableDataUri(tableDataApi.save(readAllObjects().result));
    return crudApi.executeQuery(queryInput).getTableData();
  }

  @Override
  public List<O> list(QueryInput queryInput) {
    try {
      return executeSearch(queryInput).asList(indexedObjectDefinitionClass);
    } catch (Exception e) {
      log.error("Error while searching on index " + name, e);
      return Collections.emptyList();
    }
  }

  private final SearchEntityTableDataResult readAllObjects() {
    SearchEntityTableDataResult result =
        new SearchEntityTableDataResult().searchEntityDefinition(getDefinition());

    return readAllObjects(result);
  }

  private final SearchEntityTableDataResult readAllObjects(SearchEntityTableDataResult result) {
    TableData<?> tableData = new TableData<>(result.searchEntityDefinition.definition);
    tableData.addColumns(result.searchEntityDefinition.definition.allProperties());

    Storage storage = storageApi.get(indexedObjectSchema);
    List<URI> allObjectUris = storage.readAllUris(indexedObjectDefinition().getClazz());
    allObjectUris.stream().map(u -> objectApi.load(u))
        .forEach(n -> {
          DataRow row = tableData.addRow();
          for (DataColumn<?> col : tableData.columns()) {
            PropertyMapping mapping = mappingByPropertyName.get(col.getProperty().getName());
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

    // Read all the details also.
    for (Entry<String, DetailDefinition> entry : result.searchEntityDefinition.detailsByName
        .entrySet()) {
      result.detailResults.put(entry.getKey(), readAllObjects(
          new SearchEntityTableDataResult().searchEntityDefinition(entry.getValue().detail)));
    }

    result.result = tableData;
    return result;
  }

  @Override
  public synchronized SearchEntityDefinition getDefinition() {
    if (definition == null) {
      definition = constructDefinition();
      allDefinitions(definition).forEach(e -> entityManager.registerEntityDef(e));
    }
    return definition;
  }

  private final Stream<EntityDefinition> allDefinitions(
      SearchEntityDefinition searchEntityDefinition) {
    return Stream.concat(Stream.of(searchEntityDefinition.definition),
        searchEntityDefinition.detailsByName.values().stream()
            .flatMap(d -> allDefinitions(d.detail)));
  }

  protected SearchIndexImpl(String logicalSchema, String name, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass) {
    super();
    this.name = name;
    this.logicalSchema = logicalSchema;
    this.indexedObjectSchema = indexedObjectSchema;
    this.indexedObjectDefinitionClass = indexedObjectDefinitionClass;
  }

  public abstract SearchEntityDefinition constructDefinition();

  @Override
  public String logicalSchema() {
    return logicalSchema;
  }

  @Override
  public String name() {
    return name;
  }

  public SearchIndexImpl<O> map(String propertyName, String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      mappingByPropertyName.put(propertyName, new PropertyMapping(pathes, null, null));
    }
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName,
      UnaryOperator<Object> processor,
      String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      mappingByPropertyName.put(propertyName,
          new PropertyMapping(pathes, processor, null));
    }
    return this;
  }

  public SearchIndexImpl<O> mapComplex(String propertyName,
      Function<ObjectNode, Object> complexProcessor) {
    Objects.requireNonNull(complexProcessor);
    mappingByPropertyName.put(propertyName,
        new PropertyMapping(null, null, complexProcessor));
    return this;
  }

  public SearchIndexImpl<O> expression(String propertyName,
      BiFunction<Object, Property<?>, Expression> customExpression) {
    Objects.requireNonNull(customExpression);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(customExpression, null));
    return this;
  }

  public SearchIndexImpl<O> expressionComplex(String propertyName,
      BiFunction<Object, EntityDefinition, Expression> complexExpression) {
    Objects.requireNonNull(complexExpression);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(null, complexExpression));
    return this;
  }

  protected ObjectDefinition<O> indexedObjectDefinition() {
    if (indexedObjectDefinition == null) {
      indexedObjectDefinition = objectApi.definition(indexedObjectDefinitionClass);
    }
    return indexedObjectDefinition;
  }

  @SuppressWarnings("unchecked")
  protected Expression createDynamicExpression(Class<?> propertyType, Property<?> property,
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
