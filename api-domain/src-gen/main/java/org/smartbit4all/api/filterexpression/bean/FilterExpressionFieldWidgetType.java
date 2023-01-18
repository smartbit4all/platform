/*
 * Filter API 2
 * Filter API 2
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.filterexpression.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets FilterExpressionFieldWidgetType
 */
public enum FilterExpressionFieldWidgetType {
  
  TEXT_FIELD("TEXT_FIELD"),
  
  TEXT_FIELD_CHIPS("TEXT_FIELD_CHIPS"),
  
  TEXT_BOX("TEXT_BOX"),
  
  SELECT("SELECT"),
  
  SELECT_MULTIPLE("SELECT_MULTIPLE"),
  
  DATE("DATE"),
  
  TIME("TIME"),
  
  DATE_TIME("DATE_TIME"),
  
  CHECK_BOX("CHECK_BOX"),
  
  RADIO_BUTTON("RADIO_BUTTON"),
  
  RANGE("RANGE");

  private String value;

  FilterExpressionFieldWidgetType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static FilterExpressionFieldWidgetType fromValue(String value) {
    for (FilterExpressionFieldWidgetType b : FilterExpressionFieldWidgetType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
