/*
 * The sample domain
 * Contains object for testing purposes.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.sample.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.sample.bean.SampleInlineObject;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * A container object to represent complex container hierarchy. 
 */
@ApiModel(description = "A container object to represent complex container hierarchy. ")
@JsonPropertyOrder({
  SampleContainerItem.URI,
  SampleContainerItem.NAME,
  SampleContainerItem.USER_URI,
  SampleContainerItem.INLINE_OBJECT,
  SampleContainerItem.DATASHEET,
  SampleContainerItem.MAIN_DOCUMENT,
  SampleContainerItem.ATTACHMENTS
})
@JsonTypeName("SampleContainerItem")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SampleContainerItem {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String USER_URI = "userUri";
  private URI userUri;

  public static final String INLINE_OBJECT = "inlineObject";
  private SampleInlineObject inlineObject;

  public static final String DATASHEET = "datasheet";
  private URI datasheet;

  public static final String MAIN_DOCUMENT = "mainDocument";
  private URI mainDocument;

  public static final String ATTACHMENTS = "attachments";
  private List<URI> attachments = null;


  public SampleContainerItem uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public SampleContainerItem name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setName(String name) {
    this.name = name;
  }


  public SampleContainerItem userUri(URI userUri) {
    
    this.userUri = userUri;
    return this;
  }

   /**
   * The reference that is not contained pointing to the user.
   * @return userUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The reference that is not contained pointing to the user.")
  @JsonProperty(USER_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUserUri() {
    return userUri;
  }


  @JsonProperty(USER_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUserUri(URI userUri) {
    this.userUri = userUri;
  }


  public SampleContainerItem inlineObject(SampleInlineObject inlineObject) {
    
    this.inlineObject = inlineObject;
    return this;
  }

   /**
   * Get inlineObject
   * @return inlineObject
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(INLINE_OBJECT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SampleInlineObject getInlineObject() {
    return inlineObject;
  }


  @JsonProperty(INLINE_OBJECT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInlineObject(SampleInlineObject inlineObject) {
    this.inlineObject = inlineObject;
  }


  public SampleContainerItem datasheet(URI datasheet) {
    
    this.datasheet = datasheet;
    return this;
  }

   /**
   * The data sheet contained. 
   * @return datasheet
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The data sheet contained. ")
  @JsonProperty(DATASHEET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getDatasheet() {
    return datasheet;
  }


  @JsonProperty(DATASHEET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDatasheet(URI datasheet) {
    this.datasheet = datasheet;
  }


  public SampleContainerItem mainDocument(URI mainDocument) {
    
    this.mainDocument = mainDocument;
    return this;
  }

   /**
   * The main document reference that is contained. 
   * @return mainDocument
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The main document reference that is contained. ")
  @JsonProperty(MAIN_DOCUMENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getMainDocument() {
    return mainDocument;
  }


  @JsonProperty(MAIN_DOCUMENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMainDocument(URI mainDocument) {
    this.mainDocument = mainDocument;
  }


  public SampleContainerItem attachments(List<URI> attachments) {
    
    this.attachments = attachments;
    return this;
  }

  public SampleContainerItem addAttachmentsItem(URI attachmentsItem) {
    if (this.attachments == null) {
      this.attachments = new ArrayList<>();
    }
    this.attachments.add(attachmentsItem);
    return this;
  }

   /**
   * The attachment list with document references that are contained. 
   * @return attachments
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The attachment list with document references that are contained. ")
  @JsonProperty(ATTACHMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<URI> getAttachments() {
    return attachments;
  }


  @JsonProperty(ATTACHMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAttachments(List<URI> attachments) {
    this.attachments = attachments;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SampleContainerItem sampleContainerItem = (SampleContainerItem) o;
    return Objects.equals(this.uri, sampleContainerItem.uri) &&
        Objects.equals(this.name, sampleContainerItem.name) &&
        Objects.equals(this.userUri, sampleContainerItem.userUri) &&
        Objects.equals(this.inlineObject, sampleContainerItem.inlineObject) &&
        Objects.equals(this.datasheet, sampleContainerItem.datasheet) &&
        Objects.equals(this.mainDocument, sampleContainerItem.mainDocument) &&
        Objects.equals(this.attachments, sampleContainerItem.attachments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, userUri, inlineObject, datasheet, mainDocument, attachments);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SampleContainerItem {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    userUri: ").append(toIndentedString(userUri)).append("\n");
    sb.append("    inlineObject: ").append(toIndentedString(inlineObject)).append("\n");
    sb.append("    datasheet: ").append(toIndentedString(datasheet)).append("\n");
    sb.append("    mainDocument: ").append(toIndentedString(mainDocument)).append("\n");
    sb.append("    attachments: ").append(toIndentedString(attachments)).append("\n");
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
