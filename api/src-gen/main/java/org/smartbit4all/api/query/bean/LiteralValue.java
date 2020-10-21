package org.smartbit4all.api.query.bean;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * LiteralValue
 */

public class LiteralValue   {
  @JsonProperty("value")
  private String value;

  @JsonProperty("typeQName")
  private String typeQName;

  public LiteralValue value(String value) {
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

  public LiteralValue typeQName(String typeQName) {
    this.typeQName = typeQName;
    return this;
  }

  /**
   * The qualified name of the type of the value
   * @return typeQName
  */
  @ApiModelProperty(required = true, value = "The qualified name of the type of the value")
  @NotNull


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
    LiteralValue literalValue = (LiteralValue) o;
    return Objects.equals(this.value, literalValue.value) &&
        Objects.equals(this.typeQName, literalValue.typeQName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, typeQName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LiteralValue {\n");
    
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

