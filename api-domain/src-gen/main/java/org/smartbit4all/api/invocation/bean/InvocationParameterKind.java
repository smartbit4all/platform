package org.smartbit4all.api.invocation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets InvocationParameterKind
 */
public enum InvocationParameterKind {
  
  PRIMITIVE("PRIMITIVE"),
  
  OBJECTURI("OBJECTURI"),
  
  OBJECTUUID("OBJECTUUID");

  private String value;

  InvocationParameterKind(String value) {
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
  public static InvocationParameterKind fromValue(String value) {
    for (InvocationParameterKind b : InvocationParameterKind.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

