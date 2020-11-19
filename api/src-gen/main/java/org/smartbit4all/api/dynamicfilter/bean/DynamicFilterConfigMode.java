package org.smartbit4all.api.dynamicfilter.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets DynamicFilterConfigMode
 */
public enum DynamicFilterConfigMode {
  
  STATIC("static"),
  
  SIMPLE_DYNAMIC("simple_dynamic"),
  
  DYNAMIC("dynamic"),
  
  RESEARCH("research");

  private String value;

  DynamicFilterConfigMode(String value) {
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
  public static DynamicFilterConfigMode fromValue(String value) {
    for (DynamicFilterConfigMode b : DynamicFilterConfigMode.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

