package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import org.smartbit4all.core.utility.TriFunction;
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

  protected SearchIndexMappingObject objectMapping = new SearchIndexMappingObject();

  /**
   * If we need a special mapping between the filter field of a bean the this map contains the
   * lambda for this.
   */
  protected Map<String, CustomExpressionMapping> expressionByPropertyName = new LinkedHashMap<>();

  // Pre and Post processors
  List<BiFunction<FilterExpressionList, SearchIndex<?>, FilterExpressionList>> filterExpressionListPreProcessors =
      new ArrayList<>();
  List<BiFunction<QueryInput, SearchIndex<?>, QueryInput>> queryInputPreProcessors =
      new ArrayList<>();
  List<BiFunction<TableData<?>, SearchIndex<?>, TableData<?>>> postProcessor = new ArrayList<>();;

  protected Map<String, Comparator<Object>> comparatorsByClass;

  protected String indexedObjectSchema;

  protected ObjectDefinition<O> indexedObjectDefinition;

  private boolean useDatabase;

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

  @Autowired
  protected DefaultComparatorProvider comparatorProvider;

  private boolean isComparatorSetExplicitly = false;

  public void setup(ObjectApi objectApi, StorageApi storageApi, CrudApi crudApi,
      TableDataApi tableDataApi, ApplicationContext ctx, EntityManager entityManager,
      LocaleSettingApi localeSettingApi, FilterExpressionApi filterExpressionApi,
      DefaultComparatorProvider comparatorProvider) {
    this.objectApi = objectApi;
    this.storageApi = storageApi;
    this.crudApi = crudApi;
    this.tableDataApi = tableDataApi;
    this.ctx = ctx;
    this.entityManager = entityManager;
    this.localeSettingApi = localeSettingApi;
    this.filterExpressionApi = filterExpressionApi;
    this.comparatorProvider = comparatorProvider;
  }


  /**
   * Set SearchIndex level comparators for properties by classes. This SearchIndex will not get
   * implicit comparators after this is used.
   *
   * @param comparators The map to use when comparing values of properties. Comparators by classes.
   */
  public void setComparators(Map<String, Comparator<Object>> comparators) {
    this.comparatorsByClass = comparators;
    isComparatorSetExplicitly = true;
  }

  /**
   * Set SearchIndex level comparators for properties by classes. This SearchIndex will not get
   * implicit comparators after this is used.
   *
   * @param comparators The map to use when comparing values of properties. Comparators by classes.
   */
  public SearchIndexImpl<O> comparators(Map<String, Comparator<Object>> comparators) {
    this.comparatorsByClass = comparators;
    isComparatorSetExplicitly = true;
    return this;
  }

  /**
   * Put SearchIndex level comparator for properties by class. This SearchIndex will still get
   * implicit comparators after this is used.
   *
   * @param clazz The class that will use the comparator.
   * @param comparator The comparator to be used.
   */
  public SearchIndexImpl<O> putComparator(Class<?> clazz, Comparator<Object> comparator) {
    if (comparatorsByClass == null) {
      comparatorsByClass = new HashMap<>();
    }
    this.comparatorsByClass.put(clazz.getName(), comparator);
    return this;
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

    queryInput = process(queryInput, queryInputPreProcessors);
    if ((!crudApi.isExecutionApiExists(queryInput.getEntityDef())
        && !isUseDatabase())
        || readFromStorage) {
      SearchEntityTableDataResult allObjects = readAllObjects(objectUris, objectNodes);
      if (queryInput.where() == null) {
        TableData<?> result = allObjects.result;
        if (queryInput.orderBys() != null && !queryInput.orderBys().isEmpty()) {
          tableDataApi.sort(result, queryInput.orderBys());
        }
        result = process(result, postProcessor);
        return result;
      }
      setupExists(queryInput, allObjects, Collections.emptyList());
      queryInput.setTableDataUri(tableDataApi.save(allObjects.result));
    }
    if (queryInput.where() == null) {
      queryInput.where(Expression.TRUE());
    }
    TableData<?> result = crudApi.executeQuery(queryInput).getTableData();
    result = process(result, postProcessor);

    return result;
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
      log.error("Error while searching on index " + objectMapping.getName(), e);
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
  public void updateIndexWithData(List<SearchIndexObject> changeList) {
    if (crudApi.isExecutionApiExists(getDefinition().getDefinition())
        || isUseDatabase()) {
      SearchEntityTableDataResult updateResult = constructResult();
      objectMapping.readObjects(changeList.stream().map(u -> {
        if (u.getObjectNode() == null) {
          u.objectNode(objectApi.load(u.getObjectUri()));
        }

        return u;
      }), updateResult,
          Collections.emptyMap(),
          true);
      // Update the entity definitions by the table data in the result.
      objectMapping.merge(updateResult, Collections.emptyList());

    }
  }

  @Override
  public void updateIndex(List<URI> changeList) {
    updateIndexWithData(
        changeList.stream().map(c -> new SearchIndexObject().objectUri(c)).collect(toList()));
  }

  private final SearchEntityTableDataResult readAllObjects(SearchEntityTableDataResult result,
      Stream<URI> objectUris,
      Stream<ObjectNode> objectNodes) {

    if (objectNodes == null) {
      List<URI> allObjectUris =
          objectUris == null ? getRelevantObjectUris() : objectUris.collect(toList());
      objectMapping.readObjects(
          allObjectUris.stream().map(u -> new SearchIndexObject().objectNode(objectApi.load(u))),
          result,
          Collections.emptyMap(), false);
    } else {
      objectMapping.readObjects(objectNodes.map(n -> new SearchIndexObject().objectNode(n)), result,
          Collections.emptyMap(), false);
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
    objectMapping.readObjects(uris.map(u -> new SearchIndexObject().objectNode(objectApi.load(u))),
        entityResult,
        Collections.emptyMap(),
        false);
    return entityResult.result;
  }

  @Override
  public TableData<?> tableDataOfObjects(Stream<O> objects) {
    SearchEntityTableDataResult entityResult = constructResult();
    objectMapping.readObjects(
        objects.map(o -> new SearchIndexObject().objectNode(objectApi
            .create(StringConstant.EMPTY, o))),
        entityResult,
        Collections.emptyMap(), false);
    return entityResult.result;
  }

  private void initObjectMapping() {
    objectMapping.init(ctx, entityManager, objectApi, extensionStrategy, comparatorsByClass);
  }

  private void initComparators() {
    if (comparatorProvider != null && comparatorProvider.getComparators() != null
        && !isComparatorSetExplicitly) {
      if (comparatorsByClass == null) {
        comparatorsByClass = new HashMap<>();
      }
      comparatorProvider.getComparators()
          .forEach((clazz, comp) -> comparatorsByClass.putIfAbsent(clazz, comp));
    }
  }

  @Override
  public SearchEntityDefinition getDefinition() {
    initComparators();
    initObjectMapping();
    return objectMapping.getDefinition();
  }

  private SearchIndexMappingObject getObjectMapping() {
    initComparators();
    initObjectMapping();
    return objectMapping;
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
    this.objectMapping.setName(name);
    this.objectMapping.setLogicalSchema(logicalSchema);
    this.objectMapping.filterClass(indexedObjectDefinitionClass);
    this.indexedObjectSchema = indexedObjectSchema;
    this.indexedObjectDefinitionClass = indexedObjectDefinitionClass;
    this.setUseDatabase(useDatabase);
  }

  @Override
  public String logicalSchema() {
    return objectMapping.getLogicalSchema();
  }

  @Override
  public String name() {
    return objectMapping.getName();
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
    objectMapping.map(propertyName, null, -1, null, pathes);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, Class<?> dataType, String... pathes) {
    objectMapping.map(propertyName, dataType, -1, null, pathes);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, Class<?> dataType, int length,
      String... pathes) {
    objectMapping.map(propertyName, dataType, length, null, pathes);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, Class<?> dataType,
      Comparator<Object> comparator, String... pathes) {
    objectMapping.map(propertyName, dataType, -1, comparator, pathes);
    return this;
  }

  public SearchIndexImpl<O> map(String propertyName, Class<?> dataType, int length,
      Comparator<Object> comparator,
      String... pathes) {
    objectMapping.map(propertyName, dataType, length, comparator, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName,
      UnaryOperator<Object> processor,
      String... pathes) {
    objectMapping.mapProcessed(propertyName, null, -1, null, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName, Class<?> dataType,
      UnaryOperator<Object> processor, String... pathes) {
    objectMapping.mapProcessed(propertyName, dataType, -1, null, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName, Class<?> dataType, int length,
      UnaryOperator<Object> processor, String... pathes) {
    objectMapping.mapProcessed(propertyName, dataType, length, null, processor, pathes);
    return this;
  }



  public SearchIndexImpl<O> mapProcessed(String propertyName, Class<?> dataType,
      Comparator<Object> comparator,
      UnaryOperator<Object> processor, String... pathes) {
    objectMapping.mapProcessed(propertyName, dataType, -1, comparator, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapProcessed(String propertyName, Class<?> dataType, int length,
      Comparator<Object> comparator,
      UnaryOperator<Object> processor, String... pathes) {
    objectMapping.mapProcessed(propertyName, dataType, length, comparator, processor, pathes);
    return this;
  }

  public SearchIndexImpl<O> mapComplex(String propertyName,
      Function<ObjectNode, Object> complexProcessor) {
    objectMapping.mapComplex(propertyName, String.class, -1, null, complexProcessor);
    return this;
  }

  public SearchIndexImpl<O> mapComplex(String propertyName, Class<?> dataType, int length,
      Function<ObjectNode, Object> complexProcessor) {
    objectMapping.mapComplex(propertyName, dataType, length, null, complexProcessor);
    return this;
  }

  public SearchIndexImpl<O> mapComplex(String propertyName, Class<?> dataType, int length,
      Comparator<Object> comparator,

      Function<ObjectNode, Object> complexProcessor) {
    objectMapping.mapComplex(propertyName, dataType, length, comparator, complexProcessor);
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

    filterExpressions = process(filterExpressions, filterExpressionListPreProcessors);

    Expression queryExpression =
        filterExpressions == null ? null
            : filterExpressionApi.constructExpression(
                filterExpressions, getDefinition(), getObjectMapping(), expressionByPropertyName);
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

  private <T, O> T process(T data, List<BiFunction<T, SearchIndex<?>, T>> processors) {

    T result = data;

    for (BiFunction<T, SearchIndex<?>, T> processor : processors) {
      result = processor.apply(result, this);
    }

    return result;
  }


  @Override
  public TableData<?> createEmptyTableData() {
    EntityDefinition entityDef = getDefinition().definition;
    TableData<EntityDefinition> tableData = new TableData<>(entityDef);
    tableData.addColumns(entityDef.allProperties());
    return tableData;
  }

  public SearchIndexImpl<O> expressionComplex(String propertyName,
      TriFunction<Object, EntityDefinition, SearchIndexMappingObject, Expression> complexExpression) {
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

  public SearchIndexImpl<O> preProcessFilters(
      BiFunction<FilterExpressionList, SearchIndex<?>, FilterExpressionList> processor) {
    Objects.requireNonNull(processor);
    filterExpressionListPreProcessors.add(processor);
    return this;
  }

  public SearchIndexImpl<O> preProcessQueryInput(
      BiFunction<QueryInput, SearchIndex<?>, QueryInput> processor) {
    Objects.requireNonNull(processor);
    queryInputPreProcessors.add(processor);
    return this;
  }

  public SearchIndexImpl<O> postProcess(
      BiFunction<TableData<?>, SearchIndex<?>, TableData<?>> processor) {
    Objects.requireNonNull(processor);
    postProcessor.add(processor);
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
    initComparators();
    initObjectMapping();
  }

  public SearchIndexImpl<O> primaryKey(String propertyName) {
    objectMapping.primaryKey(propertyName);
    return this;
  }

  @Override
  public SearchIndexMappingObject getSearchIndexMappingObject() {
    return objectMapping;
  }


  public boolean isUseDatabase() {
    return useDatabase;
  }

  /**
   * Use with caution, only when creating a SearchIndex based on another!
   *
   * @param useDatabase
   */
  public void setUseDatabase(boolean useDatabase) {
    this.useDatabase = useDatabase;
  }

}
