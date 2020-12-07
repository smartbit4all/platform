package org.smartbit4all.api.filter.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets FilterConfigMode
 */
public enum FilterConfigMode {

  STATIC("static"),

  SIMPLE_DYNAMIC("simple_dynamic"),

  DYNAMIC("dynamic");

  private String value;

  FilterConfigMode(String value) {
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
  public static FilterConfigMode fromValue(String value) {
    for (FilterConfigMode b : FilterConfigMode.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

