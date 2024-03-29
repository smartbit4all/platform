/*
 * Subject selector api
 * Subject selector ui api.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.bff.api.groupselector.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.org.bean.SubjectTypeDescriptor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * SubjectSelectorPageModel
 */
@JsonPropertyOrder({
  SubjectSelectorPageModel.SUJECT_MODEL_NAME,
  SubjectSelectorPageModel.SELECTED_SUBJECT_NAME,
  SubjectSelectorPageModel.SELECTED_DESCRIPTOR,
  SubjectSelectorPageModel.DESCRIPTORS
})
@JsonTypeName("SubjectSelectorPageModel")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SubjectSelectorPageModel {
  public static final String SUJECT_MODEL_NAME = "sujectModelName";
  private String sujectModelName;

  public static final String SELECTED_SUBJECT_NAME = "selectedSubjectName";
  private String selectedSubjectName;

  public static final String SELECTED_DESCRIPTOR = "selectedDescriptor";
  private SubjectTypeDescriptor selectedDescriptor = null;

  public static final String DESCRIPTORS = "descriptors";
  private Map<String, SubjectTypeDescriptor> descriptors = null;

  public SubjectSelectorPageModel() { 
  }

  public SubjectSelectorPageModel sujectModelName(String sujectModelName) {
    
    this.sujectModelName = sujectModelName;
    return this;
  }

   /**
   * Get sujectModelName
   * @return sujectModelName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SUJECT_MODEL_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSujectModelName() {
    return sujectModelName;
  }


  @JsonProperty(SUJECT_MODEL_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSujectModelName(String sujectModelName) {
    this.sujectModelName = sujectModelName;
  }


  public SubjectSelectorPageModel selectedSubjectName(String selectedSubjectName) {
    
    this.selectedSubjectName = selectedSubjectName;
    return this;
  }

   /**
   * Get selectedSubjectName
   * @return selectedSubjectName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTED_SUBJECT_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSelectedSubjectName() {
    return selectedSubjectName;
  }


  @JsonProperty(SELECTED_SUBJECT_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelectedSubjectName(String selectedSubjectName) {
    this.selectedSubjectName = selectedSubjectName;
  }


  public SubjectSelectorPageModel selectedDescriptor(SubjectTypeDescriptor selectedDescriptor) {
    
    this.selectedDescriptor = selectedDescriptor;
    return this;
  }

   /**
   * Get selectedDescriptor
   * @return selectedDescriptor
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTED_DESCRIPTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SubjectTypeDescriptor getSelectedDescriptor() {
    return selectedDescriptor;
  }


  @JsonProperty(SELECTED_DESCRIPTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelectedDescriptor(SubjectTypeDescriptor selectedDescriptor) {
    this.selectedDescriptor = selectedDescriptor;
  }


  public SubjectSelectorPageModel descriptors(Map<String, SubjectTypeDescriptor> descriptors) {
    
    this.descriptors = descriptors;
    return this;
  }

  public SubjectSelectorPageModel putDescriptorsItem(String key, SubjectTypeDescriptor descriptorsItem) {
    if (this.descriptors == null) {
      this.descriptors = new HashMap<>();
    }
    this.descriptors.put(key, descriptorsItem);
    return this;
  }

   /**
   * The subject descriptors identified by their unique name.
   * @return descriptors
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The subject descriptors identified by their unique name.")
  @JsonProperty(DESCRIPTORS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, SubjectTypeDescriptor> getDescriptors() {
    return descriptors;
  }


  @JsonProperty(DESCRIPTORS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDescriptors(Map<String, SubjectTypeDescriptor> descriptors) {
    this.descriptors = descriptors;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubjectSelectorPageModel subjectSelectorPageModel = (SubjectSelectorPageModel) o;
    return Objects.equals(this.sujectModelName, subjectSelectorPageModel.sujectModelName) &&
        Objects.equals(this.selectedSubjectName, subjectSelectorPageModel.selectedSubjectName) &&
        Objects.equals(this.selectedDescriptor, subjectSelectorPageModel.selectedDescriptor) &&
        Objects.equals(this.descriptors, subjectSelectorPageModel.descriptors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sujectModelName, selectedSubjectName, selectedDescriptor, descriptors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubjectSelectorPageModel {\n");
    sb.append("    sujectModelName: ").append(toIndentedString(sujectModelName)).append("\n");
    sb.append("    selectedSubjectName: ").append(toIndentedString(selectedSubjectName)).append("\n");
    sb.append("    selectedDescriptor: ").append(toIndentedString(selectedDescriptor)).append("\n");
    sb.append("    descriptors: ").append(toIndentedString(descriptors)).append("\n");
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

