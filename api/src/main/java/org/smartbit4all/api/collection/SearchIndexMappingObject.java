package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.SearchEntityDefinition.DetailDefinition;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBoolOperator;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldWidgetType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.object.BeanMeta;
import org.smartbit4all.core.object.BeanMetaUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.object.PathProcessor;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.JoinPath;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.meta.PropertyObject;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import com.google.common.base.Strings;

public class SearchIndexMappingObject extends SearchIndexMapping {

  private static final Logger log = LoggerFactory.getLogger(SearchIndexMappingObject.class);

  public static final String VALUE_COLUMN = "valueColumn";

  private String logicalSchema;

  private String name;

  private String tableName;

  /**
   * The name of the primary key property that must be unique in the search index. Not necessarily
   * exists but if it is set then the {@link SearchIndex} merge the database and not insert always.
   */
  String primaryKey;

  /**
   * The path of the detail property that contains a list / map, referring to another object.
   */
  String[] path;


  Function<ObjectNode, List<?>> complexProcessor;

  /**
   * If the object in the list are values like String, Long etc. In this case the
   */
  Class<?> inlineValueObjectType = null;

  /**
   * The length of the inline value object. Typically when it is a string the length of the longest
   * value to be able to store in a database.
   */
  int inlineValueObjectLength = -1;

  /**
   * {@link LinkedHashMap} to preserve the parameterization order in the entity definition.
   */
  Map<String, SearchIndexMapping> mappingsByPropertyName = new LinkedHashMap<>();

  Map<String, Comparator<Object>> comparatorsByClass;

  /**
   * The filter class is not necessary. If we have this then we can set the type of the given
   * property based on the property of this class.
   */
  Class<?> filterClass;

  /**
   * The meta of the filter class.
   */
  BeanMeta filterClassMeta;

  /**
   * The joined properties.
   */
  List<String[]> masterJoin = new ArrayList<>();

  /**
   * Mapping for references
   */
  Map<String, SearchIndexReferenceMapping> references = new HashMap<>();


  Map<String, SearchIndex<?>> referenceSearchIndices = new HashMap<>();

  /**
   * The name of the master reference.
   */
  String masterReferenceName;

  /**
   * The mapping is always points to an entity in the database side. This entity can be predefined
   * but if necessary then constructed dynamically even. We must be prepared to modify the entity
   * runtime if the setup of the search index has been changed.
   */
  SearchEntityDefinition entityDefinition;

  /**
   * The strategy to dynamically extend this instance.
   *
   * <p>
   * By default, the strategy is to not extend in any way, and return with false to avoid reloading
   * the definition.
   */
  private SearchIndexMappingExtensionStrategy extensionStrategy = self -> false;

  private ApplicationContext ctx;

  private EntityManager entityManager;

  private ObjectApi objectApi;

  private EntityDefinitionBuilder builder;

  public Set<String> getCurrentlyMappedProperties() {
    return mappingsByPropertyName.keySet();
  }

  SearchIndexMappingProperty property(String name) {
    return (SearchIndexMappingProperty) mappingsByPropertyName.get(name);
  }

  public SearchIndexMapping getProperty(String name) {
    SearchIndexMapping searchIndexMapping = mappingsByPropertyName.get(name);
    if (searchIndexMapping != null) {
      return searchIndexMapping;
    }
    PathProcessor path = PathProcessor.of(name);
    if (path.multiLevel()) {
      String root = path.firstElement();

      SearchIndexImpl<?> referenceSearchIndex =
          (SearchIndexImpl<?>) referenceSearchIndices.get(root);

      return referenceSearchIndex.getMapping().getProperty(path.ending());
    }

    return searchIndexMapping;
  }


  public SearchIndexMappingCalculatedProperty getCalculatedPropertyMapping(String propertyName) {
    SearchIndexMapping propertyMapping = getProperty(propertyName);
    if (propertyMapping instanceof SearchIndexMappingCalculatedProperty) {
      return (SearchIndexMappingCalculatedProperty) propertyMapping;
    }
    return null;
  }

  public boolean isCalculatedProperty(String propertyName) {
    SearchIndexMappingCalculatedProperty propertyMapping =
        getCalculatedPropertyMapping(propertyName);
    return propertyMapping != null;
  }

  private final Class<?> getType(String propertyName) {
    BeanMeta meta = getTypeMeta();
    if (meta != null) {
      PropertyMeta propertyMeta = meta.getProperties().get(propertyName.toUpperCase());
      if (propertyMeta != null) {
        return propertyMeta.getType();
      }
    }
    return String.class;
  }

  final synchronized BeanMeta getTypeMeta() {
    if (filterClass != null) {
      if (filterClassMeta == null) {
        filterClassMeta = BeanMetaUtil.meta(filterClass);
      }
      return filterClassMeta;
    }
    return null;
  }

  public SearchIndexMappingObject unmap(String propertyName) {
    mappingsByPropertyName.remove(propertyName);
    return this;
  }

  public SearchIndexMappingObject map(String propertyName, Class<?> dataType, int length,
      String... pathes) {
    return map(propertyName, dataType, length, null, pathes);
  }

  public SearchIndexMappingObject map(String propertyName, Class<?> dataType, int length,
      Comparator<Object> comparator,
      String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {

      mappingsByPropertyName.put(propertyName,
          new SearchIndexMappingProperty(propertyName, pathes,
              dataType == null ? getType(propertyName) : dataType, length, comparator,
              null, null, null));
    }
    return this;
  }

  public SearchIndexMappingObject mapProcessed(String propertyName, Class<?> dataType, int length,
      UnaryOperator<Object> processor,
      String... pathes) {
    return mapProcessed(propertyName, dataType, length, null, processor, pathes);
  }

  public SearchIndexMappingObject mapProcessed(String propertyName, Class<?> dataType, int length,
      Comparator<Object> comparator,
      UnaryOperator<Object> processor,
      String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      mappingsByPropertyName.put(propertyName,
          new SearchIndexMappingProperty(propertyName, pathes,
              dataType == null ? getType(propertyName) : dataType, length, comparator,
              processor, null, null));
    }
    return this;
  }

  public SearchIndexMappingObject mapComplex(String propertyName, Class<?> dataType, int length,
      Comparator<Object> comparator,
      Function<ObjectNode, Object> complexProcessor) {
    Objects.requireNonNull(complexProcessor);
    mappingsByPropertyName.put(propertyName,
        new SearchIndexMappingProperty(propertyName, null, dataType, length, comparator,
            null, complexProcessor, null));
    return this;
  }

  public SearchIndexMappingObject mapContext(String propertyName, Class<?> dataType, int length,
      Comparator<Object> comparator,
      Function<SearchIndexContext, Object> contextProcessor) {
    Objects.requireNonNull(contextProcessor);
    mappingsByPropertyName.put(propertyName,
        new SearchIndexMappingProperty(propertyName, null, dataType, length, comparator,
            null, null, contextProcessor));
    return this;
  }

  public SearchIndexMappingObject mapCalculated(String propertyName,
      List<String> dependsOnProperties, Class<?> dataType,
      Function<SearchIndexDataRowWrapper, Object> complexProcessor) {
    Objects.requireNonNull(complexProcessor);
    mappingsByPropertyName.put(propertyName,
        new SearchIndexMappingCalculatedProperty(propertyName, dependsOnProperties, dataType,
            complexProcessor));
    return this;
  }

  /**
   * Set the {@link #primaryKey} property.
   *
   * @param primaryKey
   * @return
   */
  public SearchIndexMappingObject primaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
    return this;
  }

  public String getPrimaryKey() {
    return primaryKey;
  }

  public SearchIndexMappingObject detail(String propertyName, String uniqueIdName) {
    String masterReferenceQualified = propertyName + "parent";
    SearchIndexMappingObject detail =
        new SearchIndexMappingObject().master(masterReferenceQualified,
            masterReferenceQualified,
            uniqueIdName);
    detail.init(ctx, entityManager, objectApi, extensionStrategy, comparatorsByClass);
    detail.setLogicalSchema(logicalSchema);
    detail.setName(getName() + StringConstant.UNDERLINE + propertyName);
    detail.setTableName(getTableName() + StringConstant.UNDERLINE + propertyName);
    mappingsByPropertyName.put(propertyName,
        detail);
    return detail;
  }

  public SearchEntityDefinition getDefinition() {
    if ((extensionStrategy != null && extensionStrategy.extend(this)) || entityDefinition == null) {
      initDefinition();
    }
    return entityDefinition;
  }

  protected synchronized void initDefinition() {
    entityDefinition = constructDefinition(ctx, null);
    allDefinitions(entityDefinition).forEach(e -> entityManager.registerEntityDef(e));
  }

  public EntityDefinition getEntityDefinition() {
    return getDefinition().definition;
  }

  public String getMasterReferenceName() {
    return masterReferenceName;
  }

  private final Stream<EntityDefinition> allDefinitions(
      SearchEntityDefinition searchEntityDefinition) {
    return Stream.concat(Stream.of(searchEntityDefinition.definition),
        searchEntityDefinition.detailsByName.values().stream()
            .flatMap(d -> allDefinitions(d.detail)));
  }

  SearchEntityDefinition constructDefinition(ApplicationContext ctx,
      EntityDefinitionBuilder masterBuilder) {
    if (entityDefinition != null) {
      return entityDefinition;
    }

    builder = EntityDefinitionBuilder.of(ctx)
        .name(getName())
        .tableName(getTableName())
        .domain(getLogicalSchema());

    SearchEntityDefinition result = new SearchEntityDefinition();

    if (inlineValueObjectType != null) {
      // In this case the detail object is a simple list of values with a mater id.
      // The only one
      // property of the entity definition is the value itself.
      builder.addOwnedProperty(VALUE_COLUMN, inlineValueObjectType, inlineValueObjectLength, true);
    } else {
      for (Entry<String, SearchIndexMapping> entry : mappingsByPropertyName.entrySet()) {
        if (entry.getValue() instanceof SearchIndexMappingProperty) {
          SearchIndexMappingProperty property = (SearchIndexMappingProperty) entry.getValue();
          Comparator<Object> defComparator = null;
          if (comparatorsByClass != null) {
            defComparator = comparatorsByClass.get(property.type.getName());
          }
          builder.addOwnedProperty(property.name, property.type, property.length,
              property.comparator != null ? property.comparator
                  : defComparator,
              isPrimaryKey(property.name));
        } else if (entry.getValue() instanceof SearchIndexMappingCalculatedProperty) {
          SearchIndexMappingCalculatedProperty property =
              (SearchIndexMappingCalculatedProperty) entry.getValue();
          Comparator<Object> defComparator = null;
          if (comparatorsByClass != null) {
            defComparator = comparatorsByClass.get(property.type.getName());
          }
          builder.addOwnedProperty(property.name, property.type, -1, defComparator,
              false);
        }
      }
    }

    // When all the owned properties exists.

    // We must refer to the master if any
    if (masterBuilder != null) {
      for (String[] join : masterJoin) {
        builder.addOwnedProperty(join[0],
            masterBuilder.getInstance().getProperty(join[1]).type(),
            -1,
            true);
      }
      builder.reference(masterReferenceName, masterBuilder, masterJoin);
      result.masterRef = builder.getReference(masterReferenceName);
    }

    // We construct all the details.
    for (Entry<String, SearchIndexMapping> entry : mappingsByPropertyName.entrySet()) {
      if (entry.getValue() instanceof SearchIndexMappingObject) {
        // We must setup a detail referring to this entity as master. We won't create
        // indirect
        // master join so we have only one reference in the join list.
        SearchEntityDefinition detailDefinition = ((SearchIndexMappingObject) entry.getValue())
            .constructDefinition(ctx, builder);
        ((SearchIndexMappingObject) entry.getValue()).entityDefinition = detailDefinition;
        result.detailsByName.put(entry.getKey(), new DetailDefinition(
            detailDefinition, new JoinPath(Arrays.asList(detailDefinition.masterRef))));
      }
    }

    return result.definition(builder.build());

  }

  private boolean isPrimaryKey(String propertyName) {
    return Objects.equals(primaryKey, propertyName);
  }

  final void readObjectNodes(List<ObjectNode> nodes,
      SearchEntityTableDataResult result, Map<String, Object> defaultValues, boolean useLength) {
    readObjectNodes(nodes.stream(), result, defaultValues, useLength);
  }

  final void readObjectNodes(Stream<ObjectNode> nodes,
      SearchEntityTableDataResult result, Map<String, Object> defaultValues, boolean useLength) {
    readObjects(nodes.map(d -> new SearchIndexObject().objectNode(d)),
        result, defaultValues, useLength);
  }

  final void readObjects(Stream<SearchIndexObject> objects,
      SearchEntityTableDataResult result, Map<String, Object> defaultValues, boolean useLength) {
    // reading object nodes should use the read cache
    objectApi.enableReadCache();
    try {
      // Create detail TableDatas
      if (result.manageDetails) {
        for (Entry<String, DetailDefinition> entry : result.searchEntityDefinition.detailsByName
            .entrySet()) {
          TableData<?> detailData = new TableData<>(entry.getValue().detail.definition);
          detailData.addColumns(entry.getValue().detail.definition.allProperties());
          SearchEntityTableDataResult detailResult = new SearchEntityTableDataResult()
              .searchEntityDefinition(entry.getValue().detail)
              .result(detailData);

          result.detailResults.put(entry.getKey(), detailResult);
        }
      }

      // Fill the TableDatas
      TableData<?> tableData = result.result;
      SearchIndexContext context = new SearchIndexContext();
      objects.forEach(o -> {
        ObjectNode n = o.getObjectNode();
        context.rowNode(n);
        context.getRowVariables().clear();
        DataRow row = tableData.addRow();
        Map<String, ObjectNode> referenceObjects = new HashMap<>();
        for (DataColumn<?> col : tableData.columns()) {

          if (!isCalculatedProperty(col.getName())) {
            Object value =
                readValue(context, o, col.getProperty(), referenceObjects, defaultValues,
                    useLength);
            tableData.setObject(col, row, value);
          }
        }
        // Read all the details also.
        if (result.manageDetails) {
          for (Entry<String, DetailDefinition> entry : result.searchEntityDefinition.detailsByName
              .entrySet()) {
            SearchEntityTableDataResult detailResult = result.detailResults.get(entry.getKey());
            SearchIndexMappingObject detailObjectMapping =
                ((SearchIndexMappingObject) mappingsByPropertyName
                    .get(entry.getKey()));
            if (detailObjectMapping.isInlineValueObjects()) {
              TableData<?> tableDataDetail = detailResult.result;
              Map<DataColumn<?>, Object> masterIdValues =
                  entry.getValue().masterJoin.getReferences().get(0)
                      .joins().stream()
                      .collect(toMap(
                          j -> tableDataDetail
                              .getColumn(detailObjectMapping.getDefinition().definition
                                  .getProperty(j.getSourceProperty().getName())),
                          j -> tableData.get(tableData.getColumn(j.getTargetProperty()), row)));

              List<?> valueAsList = null;
              if (detailObjectMapping.path != null
                  && detailObjectMapping.complexProcessor == null) {
                valueAsList = n.getValueAsList(detailObjectMapping.inlineValueObjectType,
                    detailObjectMapping.path);
              } else if (detailObjectMapping.complexProcessor != null) {
                valueAsList = detailObjectMapping.complexProcessor.apply(n);
              }
              if (valueAsList != null) {
                DataColumn<?> valueColumn = tableDataDetail.getColumn(
                    detailObjectMapping.getDefinition().definition.getProperty(VALUE_COLUMN));
                for (Object valueObject : valueAsList) {
                  DataRow detailRow = tableDataDetail.addRow();
                  // Set the master ids
                  masterIdValues.entrySet().stream().forEach(e -> {
                    tableDataDetail.setObject(e.getKey(), detailRow, e.getValue());
                  });
                  tableDataDetail.setObject(valueColumn, detailRow, valueObject);
                }
              }
            } else {
              detailObjectMapping.readObjectNodes(
                  n.list(detailObjectMapping.path).nodes(),
                  detailResult,
                  entry.getValue().masterJoin.getReferences().get(0).joins().stream()
                      .collect(toMap(j -> j.getSourceProperty().getName(),
                          j -> tableData.get(tableData.getColumn(j.getTargetProperty()), row))),
                  useLength);
            }
          }
        }
      });
    } finally {
      objectApi.disableReadCache();
    }
  }

  Object readValue(SearchIndexContext context, Map<String, ObjectNode> referenceObjects,
      ObjectNode n, String columName,
      Map<String, Object> defaultValues, boolean useLength) {
    Property<?> property = entityDefinition.getDefinition().getProperty(columName);
    return readValue(context, new SearchIndexObject()
        .objectNode(n),
        property,
        referenceObjects,
        defaultValues,
        useLength);
  }

  protected Object readValue(SearchIndexContext context, SearchIndexObject object,
      Property<?> property,
      Map<String, ObjectNode> referenceObjects, Map<String, Object> defaultValues,
      boolean useLength) {
    Object value = null;
    String name = property.getName();
    Object defaultValue = defaultValues.get(name);
    Object forcedValue = object.getValues().get(name);
    ObjectNode objectNode = object.getObjectNode();
    if (property instanceof PropertyRef) {
      PathProcessor propertyPath = PathProcessor.of(property.getName());
      String referenceName = propertyPath.firstElement();
      ObjectNode referenceObjectNode =
          getReferenceObject(context, object, referenceName, referenceObjects, defaultValues,
              useLength);

      ObjectNode currentRowNode = context.getRowNode();
      Map<String, Object> currentRowVariables = context.getRowVariables();
      context.setRowNode(referenceObjectNode);
      context.setRowVariables(new HashMap<>());
      SearchIndexImpl<?> referenceSearchIndex =
          (SearchIndexImpl<?>) referenceSearchIndices.get(referenceName);
      value = referenceSearchIndex.readValue(context, referenceObjects, referenceObjectNode,
          propertyPath.ending(), defaultValues, useLength);
      context.setRowNode(currentRowNode);
      context.setRowVariables(currentRowVariables);
      return value;
    } else {
      SearchIndexMappingProperty mapping = property(name);
      if (forcedValue != null) {
        value = forcedValue;
      } else if (defaultValue != null) {
        value = defaultValue;
      } else {
        if (mapping.path != null && mapping.processor == null
            && mapping.complexProcessor == null) {
          value = objectNode.getValue(mapping.path);
          if (value instanceof ObjectNodeReference) {
            value = ((ObjectNodeReference) value).getObjectUri();
          } else if (value != null && !property.type().equals(value.getClass())) {
            value = objectApi.asType(property.type(), value);
          }
        } else if (mapping.path != null && mapping.processor != null) {
          value = mapping.processor.apply(objectNode.getValue(mapping.path));
        } else if (mapping.complexProcessor != null) {
          value = mapping.complexProcessor.apply(objectNode);
        } else if (mapping.contextProcessor != null) {
          value = mapping.contextProcessor.apply(context);
        }
      }
      if (value != null && !property.type().isInstance(value)) {
        value = objectApi.asType(property.type(), value);
      }
      if (useLength && mapping.length > 0 && value instanceof String) {
        value = truncateString((String) value, mapping.length);
      }
      return value;
    }
  }

  private ObjectNode getReferenceObject(SearchIndexContext context, SearchIndexObject object,
      String referenceName,
      Map<String, ObjectNode> referenceObjects, Map<String, Object> defaultValues,
      boolean useLength) {
    ObjectNode referenceObjectNode = referenceObjects.get(referenceName);
    if (referenceObjectNode == null) {
      SearchIndexReferenceMapping referenceMapping = references.get(referenceName);
      URI referenceUri = (URI) readValue(context, object,
          entityDefinition.getDefinition().getProperty(referenceMapping.sourceProperty),
          referenceObjects, defaultValues, useLength);
      String referenceObjectName = this.name + StringConstant.DOT_REGEX + referenceName;
      // TODO use ObjectNodeReference.get
      referenceObjectNode = objectApi.load(referenceUri);
      referenceObjects.put(referenceObjectName, referenceObjectNode);
    }
    return referenceObjectNode;
  }

  public final String truncateString(String str, int maxSize) {
    if (str == null) {
      return null;
    }
    return str.length() > maxSize ? str.substring(0, maxSize) : str;
  }

  public final SearchIndexMappingObject filterClass(Class<?> filterClass) {
    this.filterClass = filterClass;
    return this;
  }

  final SearchIndexMappingObject master(String masterReferenceName,
      String sourcePropertyName, String targetPropertyName) {
    this.masterReferenceName = masterReferenceName;
    this.masterJoin = new ArrayList<>();
    this.masterJoin.add(new String[] {sourcePropertyName, targetPropertyName});
    return this;
  }

  public final void init(ApplicationContext ctx, EntityManager entityManager, ObjectApi objectApi,
      SearchIndexMappingExtensionStrategy extensionStrategy,
      Map<String, Comparator<Object>> comparatorsByClass) {
    this.ctx = ctx;
    this.entityManager = entityManager;
    this.objectApi = objectApi;
    this.extensionStrategy = extensionStrategy;
    this.comparatorsByClass = comparatorsByClass;
    for (SearchIndexMapping detailMapping : mappingsByPropertyName.values()) {
      if (detailMapping instanceof SearchIndexMappingObject) {
        ((SearchIndexMappingObject) detailMapping).init(ctx, entityManager, objectApi,
            extensionStrategy, comparatorsByClass);
      }
    }
  }

  void delete(SearchEntityTableDataResult dataToDelete, List<Object> masterRefValues) {
    PropertyObject masterReferenceProperty = null;
    if (masterReferenceName != null) {
      masterReferenceProperty =
          dataToDelete.searchEntityDefinition.definition.getPropertyObject(masterReferenceName);
    }
    if (masterReferenceProperty != null) {
      detailDelete(dataToDelete, masterReferenceProperty, masterRefValues);
    } else {
      PropertyObject primaryKeyProperty = null;
      if (primaryKey != null) {
        primaryKeyProperty = dataToDelete.searchEntityDefinition.definition
            .getPropertyObject(primaryKey);
      }
      if (primaryKeyProperty != null) {
        deleteDetails(dataToDelete);
        Crud.delete(dataToDelete.result);
      }
    }
  }

  /**
   * A recursive function that insert or update the given row in the database depending on if it is
   * exist or not.
   *
   * @param updateResult
   * @param masterRefValues
   */
  void merge(SearchEntityTableDataResult updateResult, List<Object> masterRefValues) {
    PropertyObject masterReferenceProperty = null;
    if (masterReferenceName != null) {
      masterReferenceProperty =
          updateResult.searchEntityDefinition.definition.getPropertyObject(masterReferenceName);
    }
    if (masterReferenceProperty != null) {
      detailMerge(updateResult, masterReferenceProperty, masterRefValues);
    } else {
      PropertyObject primaryKeyProperty = null;
      if (primaryKey != null) {
        primaryKeyProperty =
            updateResult.searchEntityDefinition.definition.getPropertyObject(primaryKey);
      }
      if (primaryKeyProperty != null) {
        // Execute a merge by selecting the available records first and the insert or update
        // depending
        // on the result.
        try {
          TableData<?> existingRecords =
              Crud.read(updateResult.searchEntityDefinition.definition).select(primaryKeyProperty)
                  .where(primaryKeyProperty.in(updateResult.result.values(primaryKeyProperty)))
                  .listData();
          Set<Object> existingRecordSet =
              existingRecords.values(primaryKeyProperty).stream().collect(toSet());
          PropertyObject pkProperty = primaryKeyProperty;
          List<DataRow> exitingRows = updateResult.result.rows().stream()
              .filter(r -> existingRecordSet.contains(r.get(pkProperty))).collect(toList());
          List<DataRow> notExitingRows = updateResult.result.rows().stream()
              .filter(r -> !existingRecordSet.contains(r.get(pkProperty))).collect(toList());
          if (!exitingRows.isEmpty()) {
            TableData<?> tdExisting = TableDatas.copyRows(updateResult.result, exitingRows);
            Crud.update(tdExisting);
          }
          if (!notExitingRows.isEmpty()) {
            TableData<?> tdNotExisting = TableDatas.copyRows(updateResult.result, notExitingRows);
            Crud.create(tdNotExisting);
          }
          if (updateResult.manageDetails) {
            mergeDetails(updateResult);
          }
        } catch (Exception e) {
          log.error("Unable to check the existing record for the " + getLogicalSchema()
              + StringConstant.DOT + getName() + " search index", e);
        }
      } else {
        insertAll(updateResult);
      }
    }
  }

  private void detailDelete(SearchEntityTableDataResult deleteData,
      PropertyObject masterReferenceProperty, List<Object> masterRefValues) {
    TableData<EntityDefinition> details = Crud.read(deleteData.searchEntityDefinition.definition)
        .selectAllProperties()
        .where(masterReferenceProperty.in(masterRefValues))
        .listData();
    Crud.delete(details);
  }

  private void detailMerge(SearchEntityTableDataResult updateResult,
      PropertyObject masterReferenceProperty, List<Object> masterRefValues) {
    PropertyObject valueProperty =
        updateResult.searchEntityDefinition.definition.getPropertyObject(VALUE_COLUMN);
    try {
      TableData<?> oldDetailRecords =
          Crud.read(updateResult.searchEntityDefinition.definition).selectAllProperties()
              .where(
                  masterReferenceProperty.in(masterRefValues))
              .listData();

      Set<DataRow> existingRows = new HashSet<>();
      for (DataRow oldRow : oldDetailRecords.rows()) {
        Object oldMasterRef = oldRow.get(masterReferenceProperty);
        Object oldValue = oldRow.get(valueProperty);
        for (DataRow newRow : updateResult.result.rows()) {
          Object newMasterRef = newRow.get(masterReferenceProperty);
          Object newValue = newRow.get(valueProperty);

          if (Objects.equals(oldMasterRef, newMasterRef) &&
              Objects.equals(oldValue, newValue)) {
            existingRows.add(oldRow);
          }
        }
      }

      List<DataRow> deleteRows = oldDetailRecords.rows().stream()
          .filter(r -> !existingRows.contains(r)).collect(toList());
      List<DataRow> insertRows = updateResult.result.rows().stream()
          .filter(r -> !existingRows.contains(r)).collect(toList());
      if (!deleteRows.isEmpty()) {
        TableData<?> tdDelete = TableDatas.copyRows(oldDetailRecords, deleteRows);
        Crud.delete(tdDelete);
      }
      if (!insertRows.isEmpty()) {
        TableData<?> tdInsert = TableDatas.copyRows(updateResult.result, insertRows);
        Crud.create(tdInsert);
      }
    } catch (Exception e) {
      log.error("Unable to check the existing record for the " + getLogicalSchema()
          + StringConstant.DOT + getName() + " search index", e);
    }
  }

  final void insertAll(SearchEntityTableDataResult updateResult) {
    Crud.create(updateResult.result);
    mergeDetails(updateResult);
  }

  private void deleteDetails(SearchEntityTableDataResult dataToDelete) {
    for (Entry<String, SearchIndexMapping> detailMapping : mappingsByPropertyName.entrySet()) {
      if (detailMapping.getValue() instanceof SearchIndexMappingObject) {
        SearchIndexMappingObject mappingObject =
            (SearchIndexMappingObject) detailMapping.getValue();
        Property<?> masterRefTargetProperty =
            mappingObject.entityDefinition.masterRef.joins().get(0).getTargetProperty();
        DataColumn<?> masterRefTargetColumn =
            dataToDelete.result.getColumn(masterRefTargetProperty);
        List<Object> masterRefValues =
            dataToDelete.result.values(masterRefTargetColumn).stream()
                .distinct()
                .collect(toList());
        mappingObject.delete(
            dataToDelete.detailResults.get(detailMapping.getKey()),
            masterRefValues);
      }
    }
  }

  private final void mergeDetails(SearchEntityTableDataResult updateResult) {
    for (Entry<String, SearchIndexMapping> detailMapping : mappingsByPropertyName.entrySet()) {
      if (detailMapping.getValue() instanceof SearchIndexMappingObject) {
        SearchIndexMappingObject mappingObject =
            (SearchIndexMappingObject) detailMapping.getValue();
        Property<?> masterRefTargetProperty =
            mappingObject.entityDefinition.masterRef.joins().get(0).getTargetProperty();
        DataColumn<?> masterRefTargetColumn =
            updateResult.result.getColumn(masterRefTargetProperty);

        List<Object> masterRefValues =
            updateResult.result.values(masterRefTargetColumn).stream()
                .distinct()
                .collect(toList());
        mappingObject.merge(updateResult.detailResults.get(detailMapping.getKey()),
            masterRefValues);
      }
    }
  }

  public FilterExpressionFieldList allFilterFields(LocaleSettingApi localeSettingApi) {
    return new FilterExpressionFieldList()
        .filters(mappingsByPropertyName.entrySet().stream().map(e -> {
          if (e.getValue() instanceof SearchIndexMappingProperty) {
            SearchIndexMappingProperty propertyMapping = (SearchIndexMappingProperty) e.getValue();
            FilterExpressionField field =
                new FilterExpressionField().label2(localeSettingApi.get(getName(), e.getKey()));
            field.addPossibleOperationsItem(FilterExpressionOperation.EQUAL);
            field.addPossibleOperationsItem(FilterExpressionOperation.NOT_EQUAL);
            field.addPossibleOperationsItem(FilterExpressionOperation.IS_EMPTY);
            field.addPossibleOperationsItem(FilterExpressionOperation.IS_NOT_EMPTY);
            if (propertyMapping.type != null) {
              if (String.class.isAssignableFrom(propertyMapping.type)) {
                Arrays.asList(FilterExpressionOperation.LIKE, FilterExpressionOperation.NOT_LIKE)
                    .stream().forEach(op -> field.addPossibleOperationsItem(op));
                field.widgetType(FilterExpressionFieldWidgetType.TEXT_FIELD);
                field.filterFieldType(FilterExpressionDataType.STRING);
                field.expressionData(
                    new FilterExpressionData()
                        .currentOperation(FilterExpressionOperation.LIKE)
                        .boolOperator(FilterExpressionBoolOperator.AND)
                        .operand1(new FilterExpressionOperandData().isDataName(true)
                            .type(FilterExpressionDataType.STRING)
                            .valueAsString(propertyMapping.name))
                        .operand2(new FilterExpressionOperandData().isDataName(false)
                            .type(FilterExpressionDataType.STRING)
                            .valueAsString("")));
              } else if (Long.class.isAssignableFrom(propertyMapping.type)) {
                Arrays
                    .asList(FilterExpressionOperation.GREATER,
                        FilterExpressionOperation.GREATER_OR_EQUAL,
                        FilterExpressionOperation.BETWEEN,
                        FilterExpressionOperation.NOT_BETWEEN,
                        FilterExpressionOperation.LESS, FilterExpressionOperation.LESS_OR_EQUAL)
                    .stream().forEach(op -> field.addPossibleOperationsItem(op));
              } else if (OffsetDateTime.class.isAssignableFrom(propertyMapping.type)) {
                Arrays
                    .asList(FilterExpressionOperation.GREATER,
                        FilterExpressionOperation.GREATER_OR_EQUAL,
                        FilterExpressionOperation.BETWEEN,
                        FilterExpressionOperation.NOT_BETWEEN,
                        FilterExpressionOperation.LESS, FilterExpressionOperation.LESS_OR_EQUAL)
                    .stream().forEach(op -> field.addPossibleOperationsItem(op));
                field.widgetType(FilterExpressionFieldWidgetType.DATE_TIME);
                field.filterFieldType(FilterExpressionDataType.DATE_TIME);
                field.expressionData(
                    new FilterExpressionData()
                        .currentOperation(FilterExpressionOperation.GREATER)
                        .boolOperator(FilterExpressionBoolOperator.AND)
                        .operand1(new FilterExpressionOperandData().isDataName(true)
                            .type(FilterExpressionDataType.DATE_TIME)
                            .valueAsString(propertyMapping.name))
                        .operand2(new FilterExpressionOperandData().isDataName(false)
                            .type(FilterExpressionDataType.DATE_TIME)
                            .valueAsString("")));
              }
            }
            return field;
          }
          return null;
        }).filter(f -> f != null).collect(toList()));
  }

  final boolean isInlineValueObjects() {
    return inlineValueObjectType != null;
  }

  final void setInlineValueObjects(Class<?> valueType, int length) {
    this.inlineValueObjectType = valueType;
    this.inlineValueObjectLength = length;
  }

  final void setComplexProcessor(Function<ObjectNode, List<?>> complexProcessor) {
    this.complexProcessor = complexProcessor;
  }

  public static Expression existDetailExpression(String fieldName, Object obj,
      SearchIndexMappingObject objectMapping,
      Function<PropertyObject, Expression> detailExpressionProducer) {
    SearchIndexMappingObject detailMapping =
        ((SearchIndexMappingObject) objectMapping.mappingsByPropertyName
            .get(fieldName));
    DetailDefinition detailDefinition = objectMapping.entityDefinition.detailsByName.get(fieldName);
    Expression existsExpression =
        detailExpressionProducer.apply(detailMapping.getDefinition().definition
            .getPropertyObject(SearchIndexMappingObject.VALUE_COLUMN));
    // Add the exists to the current entity and return the exists expression as is.
    return objectMapping.entityDefinition.definition
        .exists(detailDefinition.masterJoin, existsExpression)
        .name(fieldName);
  }

  public static Expression existDetailInExpression(String fieldName, Object obj,
      SearchIndexMappingObject objectMapping) {
    return existDetailExpression(fieldName, obj, objectMapping,
        prop -> prop.in((List<Object>) obj));
  }

  public static Expression existDetailEqExpression(String fieldName, Object obj,
      SearchIndexMappingObject objectMapping) {
    return existDetailExpression(fieldName, obj, objectMapping, prop -> prop.eq(obj));
  }

  public static Expression existDetailBetweenExpression(String fieldName, Object lower,
      Object upper,
      SearchIndexMappingObject objectMapping) {
    return existDetailBetweenExpression(fieldName, lower, upper, objectMapping, null);
  }

  public static Expression existDetailBetweenExpression(String fieldName, Object lower,
      Object upper,
      SearchIndexMappingObject objectMapping, String transformFnName) {
    return existDetailExpression(fieldName, lower, objectMapping,
        prop -> {
          if (!Strings.isNullOrEmpty(transformFnName)) {
            prop = prop.function(PropertyFunction.withSelfPropertyArgument(transformFnName));
          }
          return prop.between(lower, upper);
        });
  }



  public final PropertyObject propertyOf(FilterExpressionOrderBy orderBy) {
    return getDefinition().definition.getPropertyObject(orderBy.getPropertyName());
  }

  public String getLogicalSchema() {
    return logicalSchema;
  }

  /**
   * Use with caution, only when creating a SearchIndex based on another!
   *
   * @param logicalSchema
   */
  public void setLogicalSchema(String logicalSchema) {
    this.logicalSchema = logicalSchema;
  }

  public String getName() {
    return name;
  }

  /**
   * Use with caution, only when creating a SearchIndex based on another!
   *
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getTableName() {
    // tableName, if specified, otherwise name
    return StringUtils.hasText(tableName) ? tableName : name;
  }

  /**
   * Use with caution, only when creating a SearchIndex based on another!
   *
   * @param tableName
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public SearchIndexMappingObject reference(String referenceName,
      String targetSearchIndexSchema,
      String targetSearchIndexName,
      String sourcePropertyName,
      String targetPropertyName) {
    references.put(referenceName,
        new SearchIndexReferenceMapping(referenceName, sourcePropertyName, targetSearchIndexSchema,
            targetSearchIndexName, targetPropertyName));
    return this;
  }

  public void initReferences() {
    CollectionApi collectionApi = ctx.getBean(CollectionApi.class);
    for (SearchIndexReferenceMapping referenceMapping : references.values()) {
      SearchIndex<?> searchIndex = collectionApi.searchIndex(
          referenceMapping.targetSearchIndexSchema, referenceMapping.targetSearchIndexName);
      List<String[]> referenceJoins = new ArrayList<>();
      referenceJoins
          .add(new String[] {referenceMapping.sourceProperty, referenceMapping.targetProperty});
      builder.reference(referenceMapping.referenceName,
          searchIndex.getDefinition().getDefinition(), referenceJoins);
      referenceSearchIndices.put(referenceMapping.referenceName, searchIndex);
    }

  }
}
