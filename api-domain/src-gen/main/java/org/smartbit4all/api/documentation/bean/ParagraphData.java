/*
 * documentation api
 * The documentation is a basic structure for constructing hyper linked documentations. This data type and the managing api and UI panels can be used in every application for complex dokumentation. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.documentation.bean;

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
import org.smartbit4all.api.documentation.bean.ParagraphKind;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The reference is contained by the documentation and connect it with different kind of objects as paragraph. The paragraph can be contained when the container is stored in the paragraph. And even the referrer are part of the model to ensure the reactive events when a documentation is changing. 
 */
@ApiModel(description = "The reference is contained by the documentation and connect it with different kind of objects as paragraph. The paragraph can be contained when the container is stored in the paragraph. And even the referrer are part of the model to ensure the reactive events when a documentation is changing. ")
@JsonPropertyOrder({
  ParagraphData.ID,
  ParagraphData.ORDER_NO,
  ParagraphData.KIND,
  ParagraphData.CONTENT_URI,
  ParagraphData.FRAGMENT_ID,
  ParagraphData.TEXT
})
@JsonTypeName("ParagraphData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ParagraphData {
  public static final String ID = "id";
  private Integer id;

  public static final String ORDER_NO = "orderNo";
  private Integer orderNo;

  public static final String KIND = "kind";
  private ParagraphKind kind;

  public static final String CONTENT_URI = "contentUri";
  private URI contentUri;

  public static final String FRAGMENT_ID = "fragmentId";
  private String fragmentId;

  public static final String TEXT = "text";
  private String text;

  public ParagraphData() { 
  }

  public ParagraphData id(Integer id) {
    
    this.id = id;
    return this;
  }

   /**
   * The unique identifier of the given paragraph to identify inside a documentation. By default it is the serial number of the paragraph in the array. But if we modify the sort order or delete any item then it is always the largest id + 1 in case of a new paragraph. 
   * @return id
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The unique identifier of the given paragraph to identify inside a documentation. By default it is the serial number of the paragraph in the array. But if we modify the sort order or delete any item then it is always the largest id + 1 in case of a new paragraph. ")
  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getId() {
    return id;
  }


  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setId(Integer id) {
    this.id = id;
  }


  public ParagraphData orderNo(Integer orderNo) {
    
    this.orderNo = orderNo;
    return this;
  }

   /**
   * The order number in the paragraph list of the owner documentation.. 
   * @return orderNo
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The order number in the paragraph list of the owner documentation.. ")
  @JsonProperty(ORDER_NO)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getOrderNo() {
    return orderNo;
  }


  @JsonProperty(ORDER_NO)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOrderNo(Integer orderNo) {
    this.orderNo = orderNo;
  }


  public ParagraphData kind(ParagraphKind kind) {
    
    this.kind = kind;
    return this;
  }

   /**
   * Get kind
   * @return kind
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public ParagraphKind getKind() {
    return kind;
  }


  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setKind(ParagraphKind kind) {
    this.kind = kind;
  }


  public ParagraphData contentUri(URI contentUri) {
    
    this.contentUri = contentUri;
    return this;
  }

   /**
   * If the given paragraph is a reference to any other object then this is the URI.
   * @return contentUri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "If the given paragraph is a reference to any other object then this is the URI.")
  @JsonProperty(CONTENT_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getContentUri() {
    return contentUri;
  }


  @JsonProperty(CONTENT_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setContentUri(URI contentUri) {
    this.contentUri = contentUri;
  }


  public ParagraphData fragmentId(String fragmentId) {
    
    this.fragmentId = fragmentId;
    return this;
  }

   /**
   * The fragment is used to identify an element inside the given .
   * @return fragmentId
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The fragment is used to identify an element inside the given .")
  @JsonProperty(FRAGMENT_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFragmentId() {
    return fragmentId;
  }


  @JsonProperty(FRAGMENT_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFragmentId(String fragmentId) {
    this.fragmentId = fragmentId;
  }


  public ParagraphData text(String text) {
    
    this.text = text;
    return this;
  }

   /**
   * Get text
   * @return text
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(TEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getText() {
    return text;
  }


  @JsonProperty(TEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setText(String text) {
    this.text = text;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParagraphData paragraphData = (ParagraphData) o;
    return Objects.equals(this.id, paragraphData.id) &&
        Objects.equals(this.orderNo, paragraphData.orderNo) &&
        Objects.equals(this.kind, paragraphData.kind) &&
        Objects.equals(this.contentUri, paragraphData.contentUri) &&
        Objects.equals(this.fragmentId, paragraphData.fragmentId) &&
        Objects.equals(this.text, paragraphData.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, orderNo, kind, contentUri, fragmentId, text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParagraphData {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    orderNo: ").append(toIndentedString(orderNo)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    contentUri: ").append(toIndentedString(contentUri)).append("\n");
    sb.append("    fragmentId: ").append(toIndentedString(fragmentId)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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

