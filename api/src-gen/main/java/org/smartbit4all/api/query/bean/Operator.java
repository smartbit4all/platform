package org.smartbit4all.api.query.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The operator to use
 */
public enum Operator {
  
  EQ("EQ"),
  
  LT("LT"),
  
  LE("LE"),
  
  GT("GT"),
  
  GE("GE"),
  
  LIKE("LIKE");

  private String value;

  Operator(String value) {
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
  public static Operator fromValue(String value) {
    for (Operator b : Operator.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

