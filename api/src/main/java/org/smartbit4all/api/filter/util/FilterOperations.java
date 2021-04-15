package org.smartbit4all.api.filter.util;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.filter.util.Filters.OperationCode;
import org.smartbit4all.api.value.ValueUris;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

public class FilterOperations {

  private static final String FILTEROP_PREFIX = "filterop.";

  public static FilterOperation txtEquals(Property<?> property) {
    return createOperation(property, OperationCode.TXT_EQ, null);
  }

  public static FilterOperation txtLike(Property<?> property) {
    return createOperation(property, OperationCode.TXT_LIKE, null);
  }

  public static FilterOperation txtLikeMin(Property<?> property) {
    return createOperation(property, OperationCode.TXT_LIKE_MIN, null);
  }

  public static FilterOperation txtDetLikeMin(Property<?> property, EntityDefinition entityMaster,
      EntityDefinition entityFk) {
    return createDetOperation(property, entityMaster, entityFk, OperationCode.DET_TXT_LIKE_MIN, null);
  }

  public static FilterOperation dateEquals(Property<?> property) {
    return createOperation(property, OperationCode.DATE_EQ, null);
  }

  public static FilterOperation dateInterval(Property<?> property) {
    return createOperation(property, OperationCode.DATE_INTERVAL, null);
  }

  public static FilterOperation dateIntervalCb(Property<?> property) {
    return createOperation(property, OperationCode.DATE_INTERVAL_CB, null);
  }

  public static FilterOperation dateTimeEquals(Property<?> property) {
    return createOperation(property, OperationCode.DATE_TIME_EQ, null);
  }

  public static FilterOperation dateTimeInterval(Property<?> property) {
    return createOperation(property, OperationCode.DATE_TIME_INTERVAL, null);
  }

  public static FilterOperation dateTimeIntervalCb(Property<?> property) {
    return createOperation(property, OperationCode.DATE_TIME_INTERVAL_CB, null);
  }

  public static FilterOperation txtMulti(Property<?> property, String possibleValueCode) {
    URI uri = ValueUris.createPossibleValueUri(possibleValueCode, null);
    return createOperation(property, OperationCode.MULTI_SEL, uri);
  }

  public static FilterOperation txtComboBox(Property<?> property, String possibleValueCode) {
    URI uri = ValueUris.createPossibleValueUri(possibleValueCode, null);
    return createOperation(property, OperationCode.COMBO_SEL, uri);
  }

  private static FilterOperation createOperation(Property<?> property, OperationCode opCode,
      URI possibleValuesUri) {
    URI propUri1 = property == null ? null : property.getUri();
    String operationCode = opCode.getValue();
    return new FilterOperation()
        .id(UUID.randomUUID().toString())
        .operationCode(operationCode)
        .labelCode(operationCode) // TODO
        .filterView(FILTEROP_PREFIX + operationCode) // TODO
        .propertyUri1(propUri1)
        .possibleValuesUri(possibleValuesUri);
  }

  private static FilterOperation createDetOperation(Property<?> propertyOfFilter,
      EntityDefinition masterEntity, EntityDefinition fkEntity, OperationCode opCode,
      URI possibleValuesUri) {
    URI propUri1 = propertyOfFilter == null ? null : propertyOfFilter.getUri();
    URI propUri2 = propertyOfFilter == null ? null : masterEntity.getUri();
    URI propUri3 = propertyOfFilter == null ? null : fkEntity.getUri();
    String operationCode = opCode.getValue();
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
