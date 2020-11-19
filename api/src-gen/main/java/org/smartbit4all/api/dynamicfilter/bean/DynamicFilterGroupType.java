package org.smartbit4all.api.dynamicfilter.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets DynamicFilterGroupType
 */
public enum DynamicFilterGroupType {
  
  AND("AND"),
  
  OR("OR");

  private String value;

  DynamicFilterGroupType(String value) {
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
  public static DynamicFilterGroupType fromValue(String value) {
    for (DynamicFilterGroupType b : DynamicFilterGroupType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

