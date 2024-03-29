/*
 * ValidationResult api
 * Definition of the ValidationResult page and api.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.bff.api.validationresult.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.bff.api.validationresult.bean.ValidationItem;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * ValidationResultPageModel
 */
@JsonPropertyOrder({
  ValidationResultPageModel.VALIDATION_ITEMS
})
@JsonTypeName("ValidationResultPageModel")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ValidationResultPageModel {
  public static final String VALIDATION_ITEMS = "validationItems";
  private List<ValidationItem> validationItems = null;

  public ValidationResultPageModel() { 
  }

  public ValidationResultPageModel validationItems(List<ValidationItem> validationItems) {
    
    this.validationItems = validationItems;
    return this;
  }

  public ValidationResultPageModel addValidationItemsItem(ValidationItem validationItemsItem) {
    if (this.validationItems == null) {
      this.validationItems = new ArrayList<>();
    }
    this.validationItems.add(validationItemsItem);
    return this;
  }

   /**
   * List of ValidationItem holds the severity | and the localized message from the origin ObjectValidationItem
   * @return validationItems
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "List of ValidationItem holds the severity | and the localized message from the origin ObjectValidationItem")
  @JsonProperty(VALIDATION_ITEMS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<ValidationItem> getValidationItems() {
    return validationItems;
  }


  @JsonProperty(VALIDATION_ITEMS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValidationItems(List<ValidationItem> validationItems) {
    this.validationItems = validationItems;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationResultPageModel validationResultPageModel = (ValidationResultPageModel) o;
    return Objects.equals(this.validationItems, validationResultPageModel.validationItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(validationItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidationResultPageModel {\n");
    sb.append("    validationItems: ").append(toIndentedString(validationItems)).append("\n");
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

