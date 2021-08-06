package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * * `container` This is just a folder for a set of widgets.  * `reference` Can be used to manage the reference for another entity.   * `detail` A list of other entities contained by the current entity.  * `association` The association is a reference for an independent entity instance. TODO   * `text` A text widget (a string value text field).  * `text_interval` A widget that contains a pair of text properties.  * `combobox` A selectable value stored in one or more fields  * `date` The single property widget with date type.   * `date_interval` An editor with a pair of date properties.  * `number` The single property widget with number type.  * `number_interval` An editor with a pair of number properties.  * `integer` The single property widget with integer type..  * `integer_interval` An editor with a pair of integer properties.  * `survey_combo` A widget that contains a score, and optionally a comment and/or a photo. 
 */
public enum WidgetType {
  
  CONTAINER("container"),
  
  REFERENCE("reference"),
  
  DETAIL("detail"),
  
  ASSOCIATION("association"),
  
  TEXT("text"),
  
  TEXT_INTERVAL("text_interval"),
  
  COMBOBOX("combobox"),
  
  DATE("date"),
  
  DATE_INTERVAL("date_interval"),
  
  NUMBER("number"),
  
  NUMBER_INTERVAL("number_interval"),
  
  INTEGER("integer"),
  
  INTEGER_INTERVAL("integer_interval"),
  
  SURVEY_COMBO("survey_combo");

  private String value;

  WidgetType(String value) {
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
  public static WidgetType fromValue(String value) {
    for (WidgetType b : WidgetType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

