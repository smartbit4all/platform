package org.smartbit4all.api.setting.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.setting.bean.LocaleValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * LocaleSettingForLanguage
 */

public class LocaleSettingForLanguage   {
  @JsonProperty("code")
  private String code;

  @JsonProperty("valuesByKey")
  @Valid
  private List<LocaleValue> valuesByKey = new ArrayList<>();

  @JsonProperty("valuesByLiteral")
  @Valid
  private List<LocaleValue> valuesByLiteral = null;

  public LocaleSettingForLanguage code(String code) {
    this.code = code;
    return this;
  }

  /**
   * The locale code like en or hu. The key of the settings list.
   * @return code
  */
  @ApiModelProperty(required = true, value = "The locale code like en or hu. The key of the settings list.")
  @NotNull


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public LocaleSettingForLanguage valuesByKey(List<LocaleValue> valuesByKey) {
    this.valuesByKey = valuesByKey;
    return this;
  }

  public LocaleSettingForLanguage addValuesByKeyItem(LocaleValue valuesByKeyItem) {
    this.valuesByKey.add(valuesByKeyItem);
    return this;
  }

  /**
   * The locale specific values.
   * @return valuesByKey
  */
  @ApiModelProperty(required = true, value = "The locale specific values.")
  @NotNull

  @Valid

  public List<LocaleValue> getValuesByKey() {
    return valuesByKey;
  }

  public void setValuesByKey(List<LocaleValue> valuesByKey) {
    this.valuesByKey = valuesByKey;
  }

  public LocaleSettingForLanguage valuesByLiteral(List<LocaleValue> valuesByLiteral) {
    this.valuesByLiteral = valuesByLiteral;
    return this;
  }

  public LocaleSettingForLanguage addValuesByLiteralItem(LocaleValue valuesByLiteralItem) {
    if (this.valuesByLiteral == null) {
      this.valuesByLiteral = new ArrayList<>();
    }
    this.valuesByLiteral.add(valuesByLiteralItem);
    return this;
  }

  /**
   * The locale specific values.
   * @return valuesByLiteral
  */
  @ApiModelProperty(value = "The locale specific values.")

  @Valid

  public List<LocaleValue> getValuesByLiteral() {
    return valuesByLiteral;
  }

  public void setValuesByLiteral(List<LocaleValue> valuesByLiteral) {
    this.valuesByLiteral = valuesByLiteral;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocaleSettingForLanguage localeSettingForLanguage = (LocaleSettingForLanguage) o;
    return Objects.equals(this.code, localeSettingForLanguage.code) &&
        Objects.equals(this.valuesByKey, localeSettingForLanguage.valuesByKey) &&
        Objects.equals(this.valuesByLiteral, localeSettingForLanguage.valuesByLiteral);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, valuesByKey, valuesByLiteral);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocaleSettingForLanguage {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    valuesByKey: ").append(toIndentedString(valuesByKey)).append("\n");
    sb.append("    valuesByLiteral: ").append(toIndentedString(valuesByLiteral)).append("\n");
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

