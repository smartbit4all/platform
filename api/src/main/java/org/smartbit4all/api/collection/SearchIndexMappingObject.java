package org.smartbit4all.api.collection;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.SearchEntityDefinition.DetailDefinition;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBoolOperator;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldWidgetType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.object.BeanMeta;
import org.smartbit4all.core.object.BeanMetaUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.EntityDefinitionBuilder;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.JoinPath;
import org.smartbit4all.domain.meta.PropertyObject;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.context.ApplicationContext;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class SearchIndexMappingObject extends SearchIndexMapping {

  public static final String VALUE_COLUMN = "valueColumn";

  String logicalSchema;

  String name;

  /**
   * The path of the detail property that contains a list / map, referring to another object.
   */
  String[] path;

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
  SearchIndexMappingExtensionStrategy extensionStrategy = self -> false;

  private ApplicationContext ctx;

  private EntityManager entityManager;

  private ObjectApi objectApi;

  public Set<String> getCurrentlyMappedProperties() {
    return mappingsByPropertyName.keySet();
  }

  SearchIndexMappingProperty property(String name) {
    return (SearchIndexMappingProperty) mappingsByPropertyName.get(name);
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

  public SearchIndexMappingObject map(String propertyName, Class<?> dataType, int length,
      String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {

      mappingsByPropertyName.put(propertyName,
          new SearchIndexMappingProperty(propertyName, pathes,
              dataType == null ? getType(propertyName) : dataType, length, null, null));
    }
    return this;
  }

  public SearchIndexMappingObject mapProcessed(String propertyName, Class<?> dataType, int length,
      UnaryOperator<Object> processor,
      String... pathes) {
    Objects.requireNonNull(pathes);
    if (pathes.length > 0) {
      mappingsByPropertyName.put(propertyName,
          new SearchIndexMappingProperty(propertyName, pathes,
              dataType == null ? getType(propertyName) : dataType, length, processor,
              null));
    }
    return this;
  }

  public SearchIndexMappingObject mapComplex(String propertyName, Class<?> dataType, int length,
      Function<ObjectNode, Object> complexProcessor) {
    Objects.requireNonNull(complexProcessor);
    mappingsByPropertyName.put(propertyName,
        new SearchIndexMappingProperty(propertyName, null, dataType, length, null,
            complexProcessor));
    return this;
  }

  public SearchIndexMappingObject detail(String propertyName, String uniqueIdName) {
    String masterReferenceQualified = propertyName + "parent";
    SearchIndexMappingObject detail =
        new SearchIndexMappingObject().master(masterReferenceQualified,
            masterReferenceQualified,
            uniqueIdName);
    detail.logicalSchema = logicalSchema;
    detail.name = name + StringConstant.UNDERLINE + propertyName;
    mappingsByPropertyName.put(propertyName,
        detail);
    return detail;
  }

  public synchronized SearchEntityDefinition getDefinition() {
    if ((extensionStrategy != null && extensionStrategy.extend(this)) || entityDefinition == null) {
      entityDefinition = constructDefinition(ctx, null);
      allDefinitions(entityDefinition).forEach(e -> entityManager.registerEntityDef(e));
    }
    return entityDefinition;
  }



  private final Stream<EntityDefinition> allDefinitions(
      SearchEntityDefinition searchEntityDefinition) {
    return Stream.concat(Stream.of(searchEntityDefinition.definition),
        searchEntityDefinition.detailsByName.values().stream()
            .flatMap(d -> allDefinitions(d.detail)));
  }

  SearchEntityDefinition constructDefinition(ApplicationContext ctx,
      EntityDefinitionBuilder masterBuilder) {
    EntityDefinitionBuilder builder = EntityDefinitionBuilder.of(ctx)
        .name(name)
        .tableName(name)
        .domain(logicalSchema);

    if (entityDefinition != null) {
      return entityDefinition;
    }

    SearchEntityDefinition result = new SearchEntityDefinition();

    if (inlineValueObjectType != null) {
      // In this case the detail object is a simple list of values with a mater id.
      // The only one
      // property of the entity definition is the value itself.
      builder.addOwnedProperty(VALUE_COLUMN, inlineValueObjectType, inlineValueObjectLength);
    } else {
      for (Entry<String, SearchIndexMapping> entry : mappingsByPropertyName.entrySet()) {
        if (entry.getValue() instanceof SearchIndexMappingProperty) {
          SearchIndexMappingProperty property = (SearchIndexMappingProperty) entry.getValue();
          builder.addOwnedProperty(property.name, property.type, property.length);
        }
      }
    }

    // When all the owned properties exists.

    // We must refer to the master if any
    if (masterBuilder != null) {
      for (String[] join : masterJoin) {
        builder.ownedProperty(join[0], masterBuilder.getInstance().getProperty(join[1]).type());
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

  final void readObjects(Stream<ObjectNode> objects,
      SearchEntityTableDataResult result, Map<String, Object> defaultValues) {

    // Create detail TableDatas
    for (Entry<String, DetailDefinition> entry : result.searchEntityDefinition.detailsByName
        .entrySet()) {
      TableData<?> detailData = new TableData<>(entry.getValue().detail.definition);
      detailData.addColumns(entry.getValue().detail.definition.allProperties());
      SearchEntityTableDataResult detailResult = new SearchEntityTableDataResult()
          .searchEntityDefinition(entry.getValue().detail)
          .result(detailData);

      result.detailResults.put(entry.getKey(), detailResult);
    }

    // Fill the TableDatas
    TableData<?> tableData = result.result;
    objects.forEach(n -> {
      DataRow row = tableData.addRow();
      for (DataColumn<?> col : tableData.columns()) {
        Object defaultValue = defaultValues.get(col.getProperty().getName());
        if (defaultValue != null) {
          tableData.setObject(col, row, defaultValue);
        } else {
          SearchIndexMappingProperty mapping = property(col.getProperty().getName());
          if (mapping.path != null && mapping.processor == null
              && mapping.complexProcessor == null) {
            Object value = n.getValue(mapping.path);
            if (value instanceof ObjectNodeReference) {
              value = ((ObjectNodeReference) value).getObjectUri();
            } else if (value != null && !col.getProperty().type().equals(value.getClass())) {
              value = objectApi.asType(col.getProperty().type(), value);
            }
            tableData.setObject(col, row, value);
          } else if (mapping.path != null && mapping.processor != null) {
            tableData.setObject(col, row, mapping.processor.apply(n.getValue(mapping.path)));
          } else if (mapping.complexProcessor != null) {
            tableData.setObject(col, row, mapping.complexProcessor.apply(n));
          }
        }
      }
      // Read all the details also.
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
                      j -> tableDataDetail.getColumn(detailObjectMapping.getDefinition().definition
                          .getProperty(j.getSourceProperty().getName())),
                      j -> tableData.get(tableData.getColumn(j.getTargetProperty()), row)));
          List<?> valueAsList = n.getValueAsList(detailObjectMapping.inlineValueObjectType,
              detailObjectMapping.path);
          if (valueAsList != null) {
            DataColumn<?> valueColumn = tableDataDetail.getColumn(
                detailObjectMapping.getDefinition().definition.getProperty(VALUE_COLUMN));
            for (Object valueObject : valueAsList) {
              DataRow detailRow = tableDataDetail.addRow();
              // Set the master ids
              masterIdValues.entrySet().stream().forEach(e -> {
                tableDataDetail.setObject(e.getKey(), detailRow, e.getValue());
              });
              tableData.setObject(valueColumn, detailRow, valueObject);
            }
          }
        } else {
          detailObjectMapping.readObjects(n.list(detailObjectMapping.path).nodeStream(),
              detailResult,
              entry.getValue().masterJoin.getReferences().get(0).joins().stream()
                  .collect(toMap(j -> j.getSourceProperty().getName(),
                      j -> tableData.get(tableData.getColumn(j.getTargetProperty()), row))));
        }
      }
    });

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

  final Expression constructExpression(FilterExpressionList filterExpressions) {
    Expression currentExpression = null;
    FilterExpressionData prevFed = null;
    for (FilterExpressionData fed : filterExpressions.getExpressions()) {
      // Construct the Expression from the FilterExpressionData
      Expression exp = convertFilterExpression(fed);
      if (exp != null) {
        if (currentExpression != null && prevFed != null) {
          if (prevFed.getBoolOperator() == FilterExpressionBoolOperator.OR) {
            currentExpression = currentExpression.OR(exp);
          } else {
            currentExpression = currentExpression.AND(exp);
          }
        } else {
          currentExpression = exp;
        }
        prevFed = fed;
      }
    }
    return currentExpression;
  }

  private final PropertyObject propertyOf(FilterExpressionOperandData op) {
    if (op != null && Boolean.TRUE.equals(op.getIsDataName())) {
      return entityDefinition.definition.getPropertyObject(op.getValueAsString());
    }
    return null;
  }

  private final Object valueOf(FilterExpressionOperandData op, PropertyObject property)
      throws IOException {
    if (op != null && Boolean.FALSE.equals(op.getIsDataName())) {
      return convertValue(op.getValueAsString(), property);
    }
    return null;
  }

  private final Expression convertFilterExpression(FilterExpressionData fed) {
    String propertyName;
    PropertyObject property = null;
    List<PropertyObject> properties = new ArrayList<>();
    if (Arrays.asList(FilterExpressionOperation.EXISTS, FilterExpressionOperation.NOT_EXISTS)
        .contains(fed.getCurrentOperation())) {
      propertyName = fed.getOperand1().getValueAsString();
    } else {
      properties.add(propertyOf(fed.getOperand1()));
      properties.add(propertyOf(fed.getOperand2()));
      properties.add(propertyOf(fed.getOperand3()));
      // The first property would be great for type conversion.
      property = properties.stream().filter(p -> p != null).findFirst().get();
      propertyName = property.getName();
    }
    // Type conversion by the type of the filter expression operand
    List<Object> values = new ArrayList<>();
    try {
      values.add(valueOf(fed.getOperand1(), property));
      values.add(valueOf(fed.getOperand2(), property));
      values.add(valueOf(fed.getOperand3(), property));
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Unable to convert the string value of the filter expression to "
              + property.getBasic().type(),
          e);
    }

    switch (fed.getCurrentOperation()) {
      case BETWEEN:
        return properties.get(0).between(values.get(1), values.get(2));
      case EQUAL:
        return properties.get(0).eq(values.get(1));
      case EXISTS:
        // The expression is simple parenthesis for the same entity definition.
        return constructExists(fed, propertyName);
      case EXPRESSION:
        // The expression is simple parenthesis for the same entity definition.
        Expression innerExpression = constructExpression(fed.getSubExpression());
        return innerExpression != null ? new ExpressionBracket(innerExpression) : null;
      case GREATER:
        return properties.get(0).gt(values.get(1));
      case GREATER_OR_EQUAL:
        return properties.get(0).ge(values.get(1));
      case IS_EMPTY:
        return properties.get(0).isNull();
      case IS_NOT_EMPTY:
        return properties.get(0).isNotNull();
      case LESS:
        return properties.get(0).lt(values.get(1));
      case LESS_OR_EQUAL:
        return properties.get(0).le(values.get(1));
      case LIKE:
        return properties.get(0).like(values.get(1));
      case NOT_BETWEEN:
        return properties.get(0).between(values.get(1), values.get(2)).NOT();
      case NOT_EQUAL:
        return properties.get(0).noteq(values.get(1));
      case NOT_EXISTS:
        return constructExists(fed, propertyName).NOT();
      case NOT_LIKE:
        return properties.get(0).notlike(values.get(1));
      default:
        break;

    }
    return null;
  }

  private final Expression constructExists(FilterExpressionData fed, String propertyName) {
    SearchIndexMappingObject detailMapping =
        ((SearchIndexMappingObject) mappingsByPropertyName.get(propertyName));
    DetailDefinition detailDefinition = entityDefinition.detailsByName.get(propertyName);
    Expression existsExpression = detailMapping.constructExpression(fed.getSubExpression());
    // Add the exists to the current entity and return the exists expression as is.
    return entityDefinition.definition.exists(detailDefinition.masterJoin, existsExpression)
        .name(propertyName);
  }

  private final Object convertValue(String valueAsString, PropertyObject property)
      throws IOException {
    if (String.class.equals(property.getBasic().type())) {
      return valueAsString;
    }
    if (OffsetDateTime.class.equals(property.getBasic().type())) {
      // TODO maybe not the best handle offsetdatetime here
      return OffsetDateTime.parse(valueAsString);
    }
    return objectApi.getDefaultSerializer().fromString(valueAsString, property.getBasic().type());
  }

  public final void setCtx(ApplicationContext ctx) {
    this.ctx = ctx;
    for (SearchIndexMapping detailMapping : mappingsByPropertyName.values()) {
      if (detailMapping instanceof SearchIndexMappingObject) {
        ((SearchIndexMappingObject) detailMapping).setCtx(ctx);
      }
    }
  }

  public final void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public final void setObjectApi(ObjectApi objectApi) {
    this.objectApi = objectApi;
  }

  void update(SearchEntityTableDataResult updateResult) {
    Crud.update(updateResult.result);
  }

  public FilterExpressionFieldList allFilterFields(LocaleSettingApi localeSettingApi) {
    return new FilterExpressionFieldList()
        .filters(mappingsByPropertyName.entrySet().stream().map(e -> {
          if (e.getValue() instanceof SearchIndexMappingProperty) {
            SearchIndexMappingProperty propertyMapping = (SearchIndexMappingProperty) e.getValue();
            FilterExpressionField field = new FilterExpressionField().label2(localeSettingApi
                .getFirstDefined(name + StringConstant.DOT + e.getKey(), e.getKey()));
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

  public static Expression existDetailInExpression(String fieldName, Object obj,
      SearchIndexMappingObject objectMapping) {
    SearchIndexMappingObject detailMapping =
        ((SearchIndexMappingObject) objectMapping.mappingsByPropertyName
            .get(fieldName));
    DetailDefinition detailDefinition = objectMapping.entityDefinition.detailsByName.get(fieldName);
    Expression existsExpression = detailMapping.getDefinition().definition
        .getPropertyObject(SearchIndexMappingObject.VALUE_COLUMN)
        .in((List<Object>) obj);
    // Add the exists to the current entity and return the exists expression as is.
    return objectMapping.entityDefinition.definition
        .exists(detailDefinition.masterJoin, existsExpression)
        .name(fieldName);
  }

  public static Expression existDetailEqExpression(String fieldName, Object obj,
      SearchIndexMappingObject objectMapping) {
    SearchIndexMappingObject detailMapping =
        ((SearchIndexMappingObject) objectMapping.mappingsByPropertyName
            .get(fieldName));
    DetailDefinition detailDefinition = objectMapping.entityDefinition.detailsByName.get(fieldName);
    Expression existsExpression = detailMapping.getDefinition().definition
        .getPropertyObject(SearchIndexMappingObject.VALUE_COLUMN).eq(obj);
    // Add the exists to the current entity and return the exists expression as is.
    return objectMapping.entityDefinition.definition
        .exists(detailDefinition.masterJoin, existsExpression)
        .name(fieldName);

  }
}
