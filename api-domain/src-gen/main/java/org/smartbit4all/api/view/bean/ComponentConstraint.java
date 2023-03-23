/*
 * View API
 * View API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.view.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.value.bean.ValueSetData;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The component constraint is responsible for the constraints belong to a given component (textfield, combobox etc.) on the view. The UI must identify the component by the dataName that is a path of the data name separated by dot like dataSheet.field. 
 */
@ApiModel(description = "The component constraint is responsible for the constraints belong to a given component (textfield, combobox etc.) on the view. The UI must identify the component by the dataName that is a path of the data name separated by dot like dataSheet.field. ")
@JsonPropertyOrder({
  ComponentConstraint.DATA_NAME,
  ComponentConstraint.VISIBLE,
  ComponentConstraint.MANDATORY,
  ComponentConstraint.ENABLED,
  ComponentConstraint.VALUE_SET
})
@JsonTypeName("ComponentConstraint")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ComponentConstraint {
  public static final String DATA_NAME = "dataName";
  private String dataName;

  public static final String VISIBLE = "visible";
  private Boolean visible = true;

  public static final String MANDATORY = "mandatory";
  private Boolean mandatory = false;

  public static final String ENABLED = "enabled";
  private Boolean enabled = true;

  public static final String VALUE_SET = "valueSet";
  private ValueSetData valueSet = null;

  public ComponentConstraint() { 
  }

  public ComponentConstraint dataName(String dataName) {
    
    this.dataName = dataName;
    return this;
  }

   /**
   * The data name to identify the related component or componenets. If it is a path the format is the following - dataSheet.field.
   * @return dataName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The data name to identify the related component or componenets. If it is a path the format is the following - dataSheet.field.")
  @JsonProperty(DATA_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getDataName() {
    return dataName;
  }


  @JsonProperty(DATA_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDataName(String dataName) {
    this.dataName = dataName;
  }


  public ComponentConstraint visible(Boolean visible) {
    
    this.visible = visible;
    return this;
  }

   /**
   * Get visible
   * @return visible
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(VISIBLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getVisible() {
    return visible;
  }


  @JsonProperty(VISIBLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }


  public ComponentConstraint mandatory(Boolean mandatory) {
    
    this.mandatory = mandatory;
    return this;
  }

   /**
   * Get mandatory
   * @return mandatory
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(MANDATORY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getMandatory() {
    return mandatory;
  }


  @JsonProperty(MANDATORY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMandatory(Boolean mandatory) {
    this.mandatory = mandatory;
  }


  public ComponentConstraint enabled(Boolean enabled) {
    
    this.enabled = enabled;
    return this;
  }

   /**
   * Get enabled
   * @return enabled
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ENABLED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getEnabled() {
    return enabled;
  }


  @JsonProperty(ENABLED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }


  public ComponentConstraint valueSet(ValueSetData valueSet) {
    
    this.valueSet = valueSet;
    return this;
  }

   /**
   * Get valueSet
   * @return valueSet
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VALUE_SET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ValueSetData getValueSet() {
    return valueSet;
  }


  @JsonProperty(VALUE_SET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValueSet(ValueSetData valueSet) {
    this.valueSet = valueSet;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComponentConstraint componentConstraint = (ComponentConstraint) o;
    return Objects.equals(this.dataName, componentConstraint.dataName) &&
        Objects.equals(this.visible, componentConstraint.visible) &&
        Objects.equals(this.mandatory, componentConstraint.mandatory) &&
        Objects.equals(this.enabled, componentConstraint.enabled) &&
        Objects.equals(this.valueSet, componentConstraint.valueSet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataName, visible, mandatory, enabled, valueSet);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComponentConstraint {\n");
    sb.append("    dataName: ").append(toIndentedString(dataName)).append("\n");
    sb.append("    visible: ").append(toIndentedString(visible)).append("\n");
    sb.append("    mandatory: ").append(toIndentedString(mandatory)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    valueSet: ").append(toIndentedString(valueSet)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

