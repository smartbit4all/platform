package org.smartbit4all.api.invocation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * InvocationParameterData
 */

public class InvocationParameterData   {
  @JsonProperty("kind")
  private InvocationParameterKind kind;

  @JsonProperty("typeClass")
  private String typeClass;

  @JsonProperty("value")
  private String value;

  public InvocationParameterData kind(InvocationParameterKind kind) {
    this.kind = kind;
    return this;
  }

  /**
   * Get kind
   * @return kind
  */
  @ApiModelProperty(value = "")

  @Valid

  public InvocationParameterKind getKind() {
    return kind;
  }

  public void setKind(InvocationParameterKind kind) {
    this.kind = kind;
  }

  public InvocationParameterData typeClass(String typeClass) {
    this.typeClass = typeClass;
    return this;
  }

  /**
   * The fully qualified type class name of the parameter.
   * @return typeClass
  */
  @ApiModelProperty(value = "The fully qualified type class name of the parameter.")


  public String getTypeClass() {
    return typeClass;
  }

  public void setTypeClass(String typeClass) {
    this.typeClass = typeClass;
  }

  public InvocationParameterData value(String value) {
    this.value = value;
    return this;
  }

  /**
   * The value of the property
   * @return value
  */
  @ApiModelProperty(value = "The value of the property")


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvocationParameterData invocationParameterData = (InvocationParameterData) o;
    return Objects.equals(this.kind, invocationParameterData.kind) &&
        Objects.equals(this.typeClass, invocationParameterData.typeClass) &&
        Objects.equals(this.value, invocationParameterData.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, typeClass, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvocationParameterData {\n");
    
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    typeClass: ").append(toIndentedString(typeClass)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

