package org.smartbit4all.api.value.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Value
 */

public class Value   {
  @JsonProperty("code")
  private String code;

  @JsonProperty("value")
  private String value;

  @JsonProperty("typeQName")
  private String typeQName;

  public Value code(String code) {
    this.code = code;
    return this;
  }

  /**
   * The code the value can be referenced by
   * @return code
  */
  @ApiModelProperty(required = true, value = "The code the value can be referenced by")
  @NotNull


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Value value(String value) {
    this.value = value;
    return this;
  }

  /**
   * The string value of the literal
   * @return value
  */
  @ApiModelProperty(required = true, value = "The string value of the literal")
  @NotNull


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Value typeQName(String typeQName) {
    this.typeQName = typeQName;
    return this;
  }

  /**
   * The qualified name of the type of the value
   * @return typeQName
  */
  @ApiModelProperty(value = "The qualified name of the type of the value")


  public String getTypeQName() {
    return typeQName;
  }

  public void setTypeQName(String typeQName) {
    this.typeQName = typeQName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Value value = (Value) o;
    return Objects.equals(this.code, value.code) &&
        Objects.equals(this.value, value.value) &&
        Objects.equals(this.typeQName, value.typeQName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, value, typeQName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Value {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    typeQName: ").append(toIndentedString(typeQName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

