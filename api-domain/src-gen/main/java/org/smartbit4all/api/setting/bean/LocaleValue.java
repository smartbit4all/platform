package org.smartbit4all.api.setting.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * LocaleValue
 */

public class LocaleValue   {
  @JsonProperty("code")
  private String code;

  @JsonProperty("value")
  private String value;

  public LocaleValue code(String code) {
    this.code = code;
    return this;
  }

  /**
   * The code of the locale value. It can be a qualified name and a literal also.
   * @return code
  */
  @ApiModelProperty(required = true, value = "The code of the locale value. It can be a qualified name and a literal also.")
  @NotNull


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public LocaleValue value(String value) {
    this.value = value;
    return this;
  }

  /**
   * The locale specific value belongs to the code.
   * @return value
  */
  @ApiModelProperty(required = true, value = "The locale specific value belongs to the code.")
  @NotNull


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
    LocaleValue localeValue = (LocaleValue) o;
    return Objects.equals(this.code, localeValue.code) &&
        Objects.equals(this.value, localeValue.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocaleValue {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

