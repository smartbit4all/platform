package org.smartbit4all.api.filter.util;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.ValueUris;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

/**
 * To create a FilterConfig use {@link FilterConfigs#builder()} instead!
 */
public class FilterOperations {

  private static final String FILTEROP_PREFIX = "filterop.";

  public static FilterOperation txtEquals(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_TXT_EQ, null);
  }

  public static FilterOperation txtLike(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_TXT_LIKE, null);
  }

  public static FilterOperation dateEquals(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_DATE_EQ, null);
  }

  public static FilterOperation dateInterval(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_DATE_INTERVAL, null);
  }

  public static FilterOperation dateIntervalCb(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_DATE_INTERVAL_CB, null);
  }

  public static FilterOperation dateTimeEquals(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_DATE_TIME_EQ, null);
  }

  public static FilterOperation dateTimeInterval(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_DATE_TIME_INTERVAL, null);
  }

  public static FilterOperation dateTimeIntervalCb(Property<?> property) {
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_DATE_TIME_INTERVAL_CB, null);
  }

  public static FilterOperation txtMulti(Property<?> property, String possibleValueCode) {
    URI uri = ValueUris.createPossibleValueUri(possibleValueCode, null);
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_MULTI_SEL, uri);
  }

  public static FilterOperation txtComboBox(Property<?> property, String possibleValueCode) {
    URI uri = ValueUris.createPossibleValueUri(possibleValueCode, null);
    return createOperation(property, DefaultFilterOperationCodes.OPERATION_COMBO_SEL, uri);
  }

  private static FilterOperation createOperation(Property<?> property, String operationCode,
      URI possibleValuesUri) {
    URI propUri1 = property == null ? null : property.getUri();
    return new FilterOperation()
        .id(UUID.randomUUID().toString())
        .operationCode(operationCode)
        .labelCode(operationCode) // TODO
        .filterView(FILTEROP_PREFIX + operationCode) // TODO
        .propertyUri1(propUri1)
        .possibleValuesUri(possibleValuesUri);
  }

  private static FilterOperation createDetOperation(Property<?> propertyOfFilter,
      EntityDefinition masterEntity, EntityDefinition fkEntity, String operationCode,
      URI possibleValuesUri) {
    URI propUri1 = propertyOfFilter == null ? null : propertyOfFilter.getUri();
    URI propUri2 = propertyOfFilter == null ? null : masterEntity.getUri();
    URI propUri3 = propertyOfFilter == null ? null : fkEntity.getUri();
    String operationCodeWithoutDet = operationCode.substring(operationCode.indexOf(".") + 1);
    String filterView = FILTEROP_PREFIX + operationCodeWithoutDet;
    return new FilterOperation()
        .id(UUID.randomUUID().toString())
        .operationCode(operationCode)
        .labelCode(operationCodeWithoutDet)
        .filterView(filterView)
        .propertyUri1(propUri1)
        .propertyUri2(propUri2)
        .propertyUri3(propUri3)
        .possibleValuesUri(possibleValuesUri);
  }


}
