package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
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
import org.smartbit4all.domain.data.TableDatas;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author Peter Boros
 *
 * @param <O>
 */
public class SearchIndexImpl<O> implements SearchIndex<O>, InitializingBean {

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

  protected boolean useDatabase;

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
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  protected FilterExpressionApi filterExpressionApi;

  public void setup(ObjectApi objectApi, StorageApi storageApi, CrudApi crudApi,
      TableDataApi tableDataApi, ApplicationContext ctx, EntityManager entityManager,
      LocaleSettingApi localeSettingApi, FilterExpressionApi filterExpressionApi) {
    this.objectApi = objectApi;
    this.storageApi = storageApi;
    this.crudApi = crudApi;
    this.tableDataApi = tableDataApi;
    this.ctx = ctx;
    this.entityManager = entityManager;
    this.localeSettingApi = localeSettingApi;
    this.filterExpressionApi = filterExpressionApi;
  }

  @Override
  public TableData<?> executeSearch(QueryInput queryInput) {
    return executeSearch(queryInput, false, null, null);
  }

  @Override
  public TableData<?> executeSearchOn(Stream<URI> objects, FilterExpressionList filterExpressions,
      List<FilterExpressionOrderBy> orderByList) {
    return executeSearch(filterExpressions, orderByList, true, objects, null);
  }

  @Override
  public TableData<?> executeSearchOnNodes(Stream<ObjectNode> objects,
      FilterExpressionList filterExpressions, List<FilterExpressionOrderBy> orderByList) {
    return executeSearch(filterExpressions, orderByList, true, null, objects);
  }

  private TableData<?> executeSearch(QueryInput queryInput, boolean readFromStorage,
      Stream<URI> objectUris, Stream<ObjectNode> objectNodes) {
    if ((!crudApi.isExecutionApiExists(queryInput.getEntityDef())
        && !useDatabase)
        || readFromStorage) {
      SearchEntityTableDataResult allObjects = readAllObjects(objectUris, objectNodes);
      if (queryInput.where() == null) {
        TableData<?> result = allObjects.result;
        if (queryInput.orderBys() != null && !queryInput.orderBys().isEmpty()) {
          TableDatas.sort(result, queryInput.orderBys());
        }
        return result;
      }
      setupExists(queryInput, allObjects, Collections.emptyList());
      queryInput.setTableDataUri(tableDataApi.save(allObjects.result));
    }
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
    return new SearchEntityTableDataResult()
        .searchEntityDefinition(getDefinition())
        .result(createEmptyTableData());
  }

  private final SearchEntityTableDataResult readAllObjects(Stream<URI> objectUris,
      Stream<ObjectNode> objectNodes) {
    return readAllObjects(constructResult(), objectUris, objectNodes);
  }

  @Override
  public void updateIndex(List<URI> changeList) {
    SearchEntityTableDataResult updateResult = constructResult();
    objectMapping.readObjects(changeList.stream().map(u -> objectApi.load(u)), updateResult,
        Collections.emptyMap());
    // Update the entity definitions by the table data in the result.
    objectMapping.merge(updateResult);
  }

  private final SearchEntityTableDataResult readAllObjects(SearchEntityTableDataResult result,
      Stream<URI> objectUris,
      Stream<ObjectNode> objectNodes) {

    if (objectNodes == null) {
      List<URI> allObjectUris =
          objectUris == null ? getRelevantObjectUris() : objectUris.collect(toList());
      objectMapping.readObjects(allObjectUris.stream().map(u -> objectApi.load(u)), result,
          Collections.emptyMap());
    } else {
      objectMapping.readObjects(objectNodes, result,
          Collections.emptyMap());
    }

    return result;
  }

  /**
   * Produce the relevant object uris by default all the uris in the storage. But can be override
   *
   * @return
   */
  protected List<URI> getRelevantObjectUris() {
    Storage storage = storageApi.get(indexedObjectSchema);
    List<URI> allObjectUris = storage.readAllUris(indexedObjectDefinition().getClazz());
    return allObjectUris;
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

  private void initObjectMapping() {
    objectMapping.init(ctx, entityManager, objectApi, extensionStrategy);
  }

  @Override
  public SearchEntityDefinition getDefinition() {
    initObjectMapping();
    return objectMapping.getDefinition();
  }

  public SearchIndexImpl(String logicalSchema, String name, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass) {
    this(
        logicalSchema,
        name,
        indexedObjectSchema,
        indexedObjectDefinitionClass,
        false);
  }

  public SearchIndexImpl(String logicalSchema, String name, String indexedObjectSchema,
      Class<O> indexedObjectDefinitionClass, boolean useDatabase) {
    super();
    this.objectMapping.name = name;
    this.objectMapping.logicalSchema = logicalSchema;
    this.objectMapping.filterClass(indexedObjectDefinitionClass);
    this.indexedObjectSchema = indexedObjectSchema;
    this.indexedObjectDefinitionClass = indexedObjectDefinitionClass;
    this.useDatabase = useDatabase;
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

  public SearchIndexImpl<O> detailListOfValue(String propertyName, String masterUniqueId,
      Class<?> valueType, int length, Function<ObjectNode, List<?>> complexProcessor) {
    SearchIndexMappingObject detail = objectMapping.detail(propertyName, masterUniqueId);
    detail.setInlineValueObjects(valueType, length);
    detail.setComplexProcessor(complexProcessor);
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
    return executeSearch(filterExpressions, orderByList, false, null, null);
  }

  private TableData<?> executeSearch(FilterExpressionList filterExpressions,
      List<FilterExpressionOrderBy> orderByList, boolean readFromStorage,
      Stream<URI> objectUris, Stream<ObjectNode> objectNodes) {
    Expression queryExpression =
        filterExpressions == null ? null
            : filterExpressionApi.constructExpression(
                filterExpressions, getDefinition());
    CrudRead<EntityDefinition> read = Crud.read(getDefinition().definition)
        .selectAllProperties()
        .where(queryExpression);
    if (orderByList != null) {
      for (FilterExpressionOrderBy orderBy : orderByList) {
        read.order(orderBy.getOrder() == OrderEnum.DESC
            ? objectMapping.propertyOf(orderBy).desc()
            : objectMapping.propertyOf(orderBy).asc());
      }
    }
    return executeSearch(read.getQuery(), readFromStorage, objectUris, objectNodes);
  }

  @Override
  public TableData<?> createEmptyTableData() {
    EntityDefinition entityDef = getDefinition().definition;
    TableData<EntityDefinition> tableData = new TableData<>(entityDef);
    tableData.addColumns(entityDef.allProperties());
    return tableData;
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

  @Override
  public void afterPropertiesSet() throws Exception {
    initObjectMapping();
  }

  public SearchIndexImpl<O> primaryKey(String propertyName) {
    objectMapping.primaryKey(propertyName);
    return this;
  }

}
