package org.smartbit4all.api.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Bool operators fol clauses
 */
public enum BoolOperator {
  
  AND("AND"),
  
  OR("OR"),
  
  XOR("XOR");

  private String value;

  BoolOperator(String value) {
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
  public static BoolOperator fromValue(String value) {
    for (BoolOperator b : BoolOperator.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

