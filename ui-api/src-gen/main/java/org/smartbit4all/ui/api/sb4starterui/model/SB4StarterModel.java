/*
 * sb4starter ui api
 * sb4starter ui api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.ui.api.sb4starterui.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.sb4starter.bean.CommandKind;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterState;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * SB4StarterModel
 */
@JsonPropertyOrder({
  SB4StarterModel.STATE,
  SB4StarterModel.START_CONTENT,
  SB4StarterModel.RESULT_CONTENT,
  SB4StarterModel.SB4_STARTER_URL,
  SB4StarterModel.EDIT_COMMAND_KIND
})
@JsonTypeName("SB4StarterModel")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SB4StarterModel {
  public static final String STATE = "state";
  private SB4StarterState state;

  public static final String START_CONTENT = "startContent";
  private BinaryContent startContent = null;

  public static final String RESULT_CONTENT = "resultContent";
  private BinaryContent resultContent = null;

  public static final String SB4_STARTER_URL = "SB4StarterUrl";
  private String sb4StarterUrl;

  public static final String EDIT_COMMAND_KIND = "editCommandKind";
  private CommandKind editCommandKind = null;

  public SB4StarterModel() { 
  }

  public SB4StarterModel state(SB4StarterState state) {
    
    this.state = state;
    return this;
  }

   /**
   * Get state
   * @return state
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SB4StarterState getState() {
    return state;
  }


  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setState(SB4StarterState state) {
    this.state = state;
  }


  public SB4StarterModel startContent(BinaryContent startContent) {
    
    this.startContent = startContent;
    return this;
  }

   /**
   * Get startContent
   * @return startContent
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(START_CONTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public BinaryContent getStartContent() {
    return startContent;
  }


  @JsonProperty(START_CONTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStartContent(BinaryContent startContent) {
    this.startContent = startContent;
  }


  public SB4StarterModel resultContent(BinaryContent resultContent) {
    
    this.resultContent = resultContent;
    return this;
  }

   /**
   * Get resultContent
   * @return resultContent
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(RESULT_CONTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public BinaryContent getResultContent() {
    return resultContent;
  }


  @JsonProperty(RESULT_CONTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setResultContent(BinaryContent resultContent) {
    this.resultContent = resultContent;
  }


  public SB4StarterModel sb4StarterUrl(String sb4StarterUrl) {
    
    this.sb4StarterUrl = sb4StarterUrl;
    return this;
  }

   /**
   * Get sb4StarterUrl
   * @return sb4StarterUrl
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SB4_STARTER_URL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSb4StarterUrl() {
    return sb4StarterUrl;
  }


  @JsonProperty(SB4_STARTER_URL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSb4StarterUrl(String sb4StarterUrl) {
    this.sb4StarterUrl = sb4StarterUrl;
  }


  public SB4StarterModel editCommandKind(CommandKind editCommandKind) {
    
    this.editCommandKind = editCommandKind;
    return this;
  }

   /**
   * Get editCommandKind
   * @return editCommandKind
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(EDIT_COMMAND_KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public CommandKind getEditCommandKind() {
    return editCommandKind;
  }


  @JsonProperty(EDIT_COMMAND_KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEditCommandKind(CommandKind editCommandKind) {
    this.editCommandKind = editCommandKind;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SB4StarterModel sb4StarterModel = (SB4StarterModel) o;
    return Objects.equals(this.state, sb4StarterModel.state) &&
        Objects.equals(this.startContent, sb4StarterModel.startContent) &&
        Objects.equals(this.resultContent, sb4StarterModel.resultContent) &&
        Objects.equals(this.sb4StarterUrl, sb4StarterModel.sb4StarterUrl) &&
        Objects.equals(this.editCommandKind, sb4StarterModel.editCommandKind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, startContent, resultContent, sb4StarterUrl, editCommandKind);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SB4StarterModel {\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    startContent: ").append(toIndentedString(startContent)).append("\n");
    sb.append("    resultContent: ").append(toIndentedString(resultContent)).append("\n");
    sb.append("    sb4StarterUrl: ").append(toIndentedString(sb4StarterUrl)).append("\n");
    sb.append("    editCommandKind: ").append(toIndentedString(editCommandKind)).append("\n");
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

