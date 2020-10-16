package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.JDBCDataConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("OperandLiteral")
public abstract class OperandLiteralMixin<T> implements OperandMixin<T> {

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
  @JsonProperty
  T value;

  @JsonProperty
  JDBCDataConverter<T, ?> typeHandler;

  @JsonCreator
  public OperandLiteralMixin(
      @JsonProperty("value") T value,
      @JsonProperty("typeHandler") JDBCDataConverter<T, ?> typeHandler) {
    this.value = value;
    this.typeHandler = typeHandler;
  }
}
