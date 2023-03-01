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
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy.OrderEnum;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
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
import org.smartbit4all.domain.utility.crud.CrudRead;
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

    BiFunction<Object, SearchIndexMappingObject, Expression> detailExpressionProcessor;

    public CustomExpressionMapping(
        BiFunction<Object, Property<?>, Expression> customExpressionProcessor,
        BiFunction<Object, EntityDefinition, Expression> complexExpressionProcessor,
        BiFunction<Object, SearchIndexMappingObject, Expression> detailExpressionProcessor) {
      super();
      this.expressionProcessor = customExpressionProcessor;
      this.complexExpressionProcessor = complexExpressionProcessor;
      this.detailExpressionProcessor = detailExpressionProcessor;
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

  protected SearchIndexMappingExtensionStrategy extensionStrategy;

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

  @Autowired
  LocaleSettingApi localeSettingApi;

  /**
   * The entity definition the search working on.
   */
  protected SearchEntityDefinition definition;

  @Override
  public TableData<?> executeSearch(QueryInput queryInput) {
    SearchEntityTableDataResult allObjects = readAllObjects();
    setupExists(queryInput, allObjects, Collections.emptyList());
    queryInput.setTableDataUri(tableDataApi.save(allObjects.result));
    if (queryInput.where() == null) {
      queryInput.where(Expression.TRUE());
    }
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
  public TableData<?> tableDataOfUris(Stream<URI> uris) {
    SearchEntityTableDataResult entityResult = constructResult();
    objectMapping.readObjects(uris.map(u -> objectApi.load(u)), entityResult,
        Collections.emptyMap());
    return entityResult.result;
  }

  @Override
  public TableData<?> tableDataOfObjects(Stream<O> objects) {
    SearchEntityTableDataResult entityResult = constructResult();
    objectMapping.readObjects(
        objects.map(o -> objectApi
            .create(StringConstant.EMPTY, o)),
        entityResult,
        Collections.emptyMap());
    return entityResult.result;
  }

  @Override
  public SearchEntityDefinition getDefinition() {
    objectMapping.setCtx(ctx);
    objectMapping.setEntityManager(entityManager);
    objectMapping.setObjectApi(objectApi);
    objectMapping.extensionStrategy = extensionStrategy;
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

  public SearchIndexMappingObject detail(String propertyName, String masterUniqueId,
      String... path) {
    SearchIndexMappingObject result = objectMapping.detail(propertyName, masterUniqueId);
    result.path = path;
    return result;
  }

  public SearchIndexImpl<O> detailListOfValue(String propertyName, String masterUniqueId,
      Class<?> valueType, int length, String... path) {
    SearchIndexMappingObject detail = objectMapping.detail(propertyName, masterUniqueId);
    detail.path = path;
    detail.setInlineValueObjects(valueType, length);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, String... pathes) {
    objectMapping.map(propertyName, null, -1, pathes);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, Class<?> dataType, String... pathes) {
    objectMapping.map(propertyName, dataType, -1, pathes);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, Class<?> dataType, int length,
      String... pathes) {
    objectMapping.map(propertyName, dataType, length, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName,
      UnaryOperator<Object> processor,
      String... pathes) {
    objectMapping.mapProcessed(propertyName, null, -1, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName, Class<?> dataType,
      UnaryOperator<Object> processor, String... pathes) {
    objectMapping.mapProcessed(propertyName, dataType, -1, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName, Class<?> dataType, int length,
      UnaryOperator<Object> processor, String... pathes) {
    objectMapping.mapProcessed(propertyName, dataType, length, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapComplex(String propertyName,
      Function<ObjectNode, Object> complexProcessor) {
    objectMapping.mapComplex(propertyName, String.class, -1, complexProcessor);
    return this;
  }

  public SearchIndexImpl<O> mapComplex(String propertyName, Class<?> dataType, int length,
      Function<ObjectNode, Object> complexProcessor) {
    objectMapping.mapComplex(propertyName, dataType, length, complexProcessor);
    return this;
  }

  public SearchIndexImpl<O> extendMapping(SearchIndexMappingExtensionStrategy extensionStrategy) {
    this.extensionStrategy = extensionStrategy;
    return this;
  }

  public SearchIndexImpl<O> expression(String propertyName,
      BiFunction<Object, Property<?>, Expression> customExpression) {
    Objects.requireNonNull(customExpression);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(customExpression, null, null));
    return this;
  }

  public SearchIndexMappingObject getMapping() {
    return objectMapping;
  }

  @Override
  public TableData<?> executeSearch(FilterExpressionList filterExpressions,
      List<FilterExpressionOrderBy> orderByList) {
    if (filterExpressions == null) {
      return getAll();
    }
    CrudRead<EntityDefinition> read = Crud.read(getDefinition().definition).selectAllProperties()
        .where(objectMapping.constructExpression(filterExpressions));
    if (orderByList != null) {
      orderByList.stream()
          .map(orderBy -> orderBy.getOrder() == OrderEnum.DESC
              ? objectMapping.propertyOf(orderBy).desc()
              : objectMapping.propertyOf(orderBy).asc())
          .forEach(read::order);
    }
    return executeSearch(read.getQuery());
  }

  public SearchIndexImpl<O> expressionComplex(String propertyName,
      BiFunction<Object, EntityDefinition, Expression> complexExpression) {
    Objects.requireNonNull(complexExpression);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(null, complexExpression, null));
    return this;
  }

  public SearchIndexImpl<O> expressionDetail(String propertyName,
      BiFunction<Object, SearchIndexMappingObject, Expression> detailExpressionProcessor) {
    Objects.requireNonNull(detailExpressionProcessor);
    expressionByPropertyName.put(propertyName,
        new CustomExpressionMapping(null, null, detailExpressionProcessor));
    return this;
  }

  protected ObjectDefinition<O> indexedObjectDefinition() {
    if (indexedObjectDefinition == null) {
      indexedObjectDefinition = objectApi.definition(indexedObjectDefinitionClass);
    }
    return indexedObjectDefinition;
  }

  @Override
  public FilterExpressionFieldList allFilterFields() {
    return objectMapping.allFilterFields(localeSettingApi);
  }

}
