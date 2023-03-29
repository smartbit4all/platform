/*
 * Form layout definition
 * Contains form layout definition objects.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.formdefinition.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets SmartFormWidgetType
 */
public enum SmartFormWidgetType {
  
  CHECK_BOX("CHECK_BOX"),
  
  CONTAINER("CONTAINER"),
  
  DATE_PICKER("DATE_PICKER"),
  
  DATE_TIME_PICKER("DATE_TIME_PICKER"),
  
  INDICATOR("INDICATOR"),
  
  ITEM("ITEM"),
  
  ITEM_GROUP("ITEM_GROUP"),
  
  LABEL("LABEL"),
  
  RADIO_BUTTON("RADIO_BUTTON"),
  
  SELECT("SELECT"),
  
  SELECT_MULTIPLE("SELECT_MULTIPLE"),
  
  TEXT_BOX("TEXT_BOX"),
  
  TEXT_FIELD("TEXT_FIELD"),
  
  TEXT_FIELD_CHIPS("TEXT_FIELD_CHIPS"),
  
  TEXT_FIELD_NUMBER("TEXT_FIELD_NUMBER"),
  
  TIME("TIME"),
  
  TOGGLE("TOGGLE");

  private String value;

  SmartFormWidgetType(String value) {
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
  public static SmartFormWidgetType fromValue(String value) {
    for (SmartFormWidgetType b : SmartFormWidgetType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
