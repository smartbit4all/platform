package org.smartbit4all.api.filter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Defines a possible way of using a filter field, with specifying Properties as well. For example: exact match, like, sounds like, interval, etc.
 */
@ApiModel(description = "Defines a possible way of using a filter field, with specifying Properties as well. For example: exact match, like, sounds like, interval, etc.")

public class FilterOperation   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("filterView")
  private String filterView;

  @JsonProperty("propertyUri1")
  private URI propertyUri1;

  @JsonProperty("propertyUri2")
  private URI propertyUri2;

  @JsonProperty("propertyUri3")
  private URI propertyUri3;

  @JsonProperty("possibleValuesUri")
  private URI possibleValuesUri;

  @JsonProperty("operationCode")
  private String operationCode;

  @JsonProperty("labelCode")
  private String labelCode;

  @JsonProperty("iconCode")
  private String iconCode;

  public FilterOperation id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of the filter operation within the possible filter operations list of a filter field.
   * @return id
  */
  @ApiModelProperty(value = "Identifier of the filter operation within the possible filter operations list of a filter field.")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FilterOperation filterView(String filterView) {
    this.filterView = filterView;
    return this;
  }

  /**
   * Declarative name of a FilterOperationUI, which will handle this operation.
   * @return filterView
  */
  @ApiModelProperty(value = "Declarative name of a FilterOperationUI, which will handle this operation.")


  public String getFilterView() {
    return filterView;
  }

  public void setFilterView(String filterView) {
    this.filterView = filterView;
  }

  public FilterOperation propertyUri1(URI propertyUri1) {
    this.propertyUri1 = propertyUri1;
    return this;
  }

  /**
   * Property identifier, specifies which property should be used in this filter.
   * @return propertyUri1
  */
  @ApiModelProperty(value = "Property identifier, specifies which property should be used in this filter.")

  @Valid

  public URI getPropertyUri1() {
    return propertyUri1;
  }

  public void setPropertyUri1(URI propertyUri1) {
    this.propertyUri1 = propertyUri1;
  }

  public FilterOperation propertyUri2(URI propertyUri2) {
    this.propertyUri2 = propertyUri2;
    return this;
  }

  /**
   * Property identifier, specifies which property should be used in this filter.
   * @return propertyUri2
  */
  @ApiModelProperty(value = "Property identifier, specifies which property should be used in this filter.")

  @Valid

  public URI getPropertyUri2() {
    return propertyUri2;
  }

  public void setPropertyUri2(URI propertyUri2) {
    this.propertyUri2 = propertyUri2;
  }

  public FilterOperation propertyUri3(URI propertyUri3) {
    this.propertyUri3 = propertyUri3;
    return this;
  }

  /**
   * Property identifier, specifies which property should be used in this filter.
   * @return propertyUri3
  */
  @ApiModelProperty(value = "Property identifier, specifies which property should be used in this filter.")

  @Valid

  public URI getPropertyUri3() {
    return propertyUri3;
  }

  public void setPropertyUri3(URI propertyUri3) {
    this.propertyUri3 = propertyUri3;
  }

  public FilterOperation possibleValuesUri(URI possibleValuesUri) {
    this.possibleValuesUri = possibleValuesUri;
    return this;
  }

  /**
   * Value set identifer for selections. Values can be aquired by ValueAPI.
   * @return possibleValuesUri
  */
  @ApiModelProperty(value = "Value set identifer for selections. Values can be aquired by ValueAPI.")

  @Valid

  public URI getPossibleValuesUri() {
    return possibleValuesUri;
  }

  public void setPossibleValuesUri(URI possibleValuesUri) {
    this.possibleValuesUri = possibleValuesUri;
  }

  public FilterOperation operationCode(String operationCode) {
    this.operationCode = operationCode;
    return this;
  }

  /**
   * Operation code, specifies the operator of the condition.
   * @return operationCode
  */
  @ApiModelProperty(value = "Operation code, specifies the operator of the condition.")


  public String getOperationCode() {
    return operationCode;
  }

  public void setOperationCode(String operationCode) {
    this.operationCode = operationCode;
  }

  public FilterOperation labelCode(String labelCode) {
    this.labelCode = labelCode;
    return this;
  }

  /**
   * Code of label to display for this operation.
   * @return labelCode
  */
  @ApiModelProperty(value = "Code of label to display for this operation.")


  public String getLabelCode() {
    return labelCode;
  }

  public void setLabelCode(String labelCode) {
    this.labelCode = labelCode;
  }

  public FilterOperation iconCode(String iconCode) {
    this.iconCode = iconCode;
    return this;
  }

  /**
   * Icon
   * @return iconCode
  */
  @ApiModelProperty(value = "Icon")


  public String getIconCode() {
    return iconCode;
  }

  public void setIconCode(String iconCode) {
    this.iconCode = iconCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterOperation filterOperation = (FilterOperation) o;
    return Objects.equals(this.id, filterOperation.id) &&
        Objects.equals(this.filterView, filterOperation.filterView) &&
        Objects.equals(this.propertyUri1, filterOperation.propertyUri1) &&
        Objects.equals(this.propertyUri2, filterOperation.propertyUri2) &&
        Objects.equals(this.propertyUri3, filterOperation.propertyUri3) &&
        Objects.equals(this.possibleValuesUri, filterOperation.possibleValuesUri) &&
        Objects.equals(this.operationCode, filterOperation.operationCode) &&
        Objects.equals(this.labelCode, filterOperation.labelCode) &&
        Objects.equals(this.iconCode, filterOperation.iconCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, filterView, propertyUri1, propertyUri2, propertyUri3, possibleValuesUri, operationCode, labelCode, iconCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterOperation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    filterView: ").append(toIndentedString(filterView)).append("\n");
    sb.append("    propertyUri1: ").append(toIndentedString(propertyUri1)).append("\n");
    sb.append("    propertyUri2: ").append(toIndentedString(propertyUri2)).append("\n");
    sb.append("    propertyUri3: ").append(toIndentedString(propertyUri3)).append("\n");
    sb.append("    possibleValuesUri: ").append(toIndentedString(possibleValuesUri)).append("\n");
    sb.append("    operationCode: ").append(toIndentedString(operationCode)).append("\n");
    sb.append("    labelCode: ").append(toIndentedString(labelCode)).append("\n");
    sb.append("    iconCode: ").append(toIndentedString(iconCode)).append("\n");
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

