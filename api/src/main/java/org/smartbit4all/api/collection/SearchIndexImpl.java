package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
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
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author Peter Boros
 *
 * @param <O>
 */
public class SearchIndexImpl<O> implements SearchIndex<O> {

  private static final Logger log = LoggerFactory.getLogger(SearchIndexImpl.class);

  private Class<O> indexedObjectDefinitionClass;

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

  protected SearchIndexMappingObject objectMapping = new SearchIndexMappingObject();

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
    SearchEntityTableDataResult allObjects = readAllObjects();
    setupExists(queryInput, allObjects, Collections.emptyList());
    queryInput.setTableDataUri(tableDataApi.save(allObjects.result));
    return crudApi.executeQuery(queryInput).getTableData();
  }

  private void setupExists(QueryInput queryInput, SearchEntityTableDataResult objectResult,
      List<String> path) {
    for (Entry<String, SearchEntityTableDataResult> entry : objectResult.detailResults
        .entrySet()) {
      URI detailTableDataUri = tableDataApi.save(entry.getValue().result);
      ArrayList<String> subPath = new ArrayList<>(path);
      subPath.add(entry.getKey());
      queryInput.setTableDataUri(detailTableDataUri, subPath.toArray(new String[subPath.size()]));
      setupExists(queryInput, entry.getValue(), subPath);
    }
  }

  public TableData<?> getAll() {
    return readAllObjects().result;
  }

  @Override
  public List<O> list(QueryInput queryInput) {
    try {
      return executeSearch(queryInput).asList(indexedObjectDefinitionClass);
    } catch (Exception e) {
      log.error("Error while searching on index " + objectMapping.name, e);
      return Collections.emptyList();
    }
  }

  private final SearchEntityTableDataResult constructResult() {
    SearchEntityTableDataResult result =
        new SearchEntityTableDataResult().searchEntityDefinition(getDefinition());

    TableData<?> tableData = new TableData<>(result.searchEntityDefinition.definition);
    tableData.addColumns(result.searchEntityDefinition.definition.allProperties());

    result.result = tableData;
    return result;
  }

  private final SearchEntityTableDataResult readAllObjects() {
    return readAllObjects(constructResult());
  }

  private final void updateIndex(List<URI> changeList) {
    SearchEntityTableDataResult updateResult = constructResult();
    objectMapping.readObjects(changeList.stream().map(u -> objectApi.load(u)), updateResult,
        Collections.emptyMap());
    // Update the entity definitions by the table data in the result.
    objectMapping.update(updateResult);
  }

  private final SearchEntityTableDataResult readAllObjects(SearchEntityTableDataResult result) {

    Storage storage = storageApi.get(indexedObjectSchema);
    List<URI> allObjectUris = storage.readAllUris(indexedObjectDefinition().getClazz());
    objectMapping.readObjects(allObjectUris.stream().map(u -> objectApi.load(u)), result,
        Collections.emptyMap());

    return result;
  }

  @Override
  public SearchEntityDefinition getDefinition() {
    objectMapping.setCtx(ctx);
    objectMapping.setEntityManager(entityManager);
    objectMapping.setObjectApi(objectApi);
    return objectMapping.getDefinition();
  }

  public SearchIndexImpl(String logicalSchema, String name, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass) {
    super();
    this.objectMapping.name = name;
    this.objectMapping.logicalSchema = logicalSchema;
    this.objectMapping.filterClass(indexedObjectDefinitionClass);
    this.indexedObjectSchema = indexedObjectSchema;
    this.indexedObjectDefinitionClass = indexedObjectDefinitionClass;
  }

  @Override
  public String logicalSchema() {
    return objectMapping.logicalSchema;
  }

  @Override
  public String name() {
    return objectMapping.name;
  }

  public SearchIndexMappingObject detail(String propertyName, String uniqueIdName) {
    return objectMapping.detail(propertyName, uniqueIdName);
  }

  public SearchIndexImpl<O> map(String propertyName, String... pathes) {
    objectMapping.map(propertyName, null, pathes);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, Class<?> dataType, String... pathes) {
    objectMapping.map(propertyName, dataType, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName,
      UnaryOperator<Object> processor,
      String... pathes) {
    objectMapping.mapProcessed(propertyName, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapComplex(String propertyName,
      Function<ObjectNode, Object> complexProcessor) {
    objectMapping.mapComplex(propertyName, complexProcessor);
    return this;
  }

  public SearchIndexImpl<O> expression(String propertyName,
      BiFunction<Object, Property<?>, Expression> customExpression) {
    Objects.requireNonNull(customExpression);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(customExpression, null));
    return this;
  }

  public SearchIndexMappingObject getMapping() {
    return objectMapping;
  }

  @Override
  public TableData<?> executeSearch(FilterExpressionList filterExpressions) {
    if (filterExpressions == null) {
      return getAll();
    }
    return executeSearch(Crud.read(getDefinition().definition).selectAllProperties()
        .where(objectMapping.constructExpression(filterExpressions)).getQuery());
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
