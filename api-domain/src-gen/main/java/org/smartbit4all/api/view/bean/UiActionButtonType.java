/*
 * View API
 * View API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.view.bean;

import java.util.Objects;
import java.util.Arrays;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the type of the button. 
 */
public enum UiActionButtonType {
  
  NORMAL("NORMAL"),
  
  FLAT("FLAT"),
  
  STROKED("STROKED"),
  
  RAISED("RAISED"),
  
  ICON("ICON"),
  
  MINI_FAB("MINI_FAB"),
  
  FAB("FAB"),
  
  SEPARATOR("SEPARATOR"),
  
  SUBMENU("SUBMENU");

  private String value;

  UiActionButtonType(String value) {
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
  public static UiActionButtonType fromValue(String value) {
    for (UiActionButtonType b : UiActionButtonType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

