package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets FilterLabelPosition
 */
public enum FilterLabelPosition {
  
  PLACEHOLDER("placeholder"),
  
  ON_TOP("on_top"),
  
  ON_LEFT("on_left");

  private String value;

  FilterLabelPosition(String value) {
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
  public static FilterLabelPosition fromValue(String value) {
    for (FilterLabelPosition b : FilterLabelPosition.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

