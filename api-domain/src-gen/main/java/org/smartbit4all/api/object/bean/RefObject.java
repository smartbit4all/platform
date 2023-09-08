/*
 * Object api
 * The object api responsible for the domain object meta information including the object definitions and the relations among them. These objects are stored because the modules can contribute. The modules have their own ObjectApi that manages the storage and ensure the up-to-date view of the current data. The algorithms are running on the ObjectApi cache refreshed periodically. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.object.bean;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Wraps an object URI defining a qualified association between two domain objects.  It is meant to be stored inline in an object denoting a unidirectional qualified reference. The association may reference a link object, which is a standalone persisted object, describing the qualities and participant of a multi-directional binding between aggregate roots. If such link object is present, all objects bound together by it are expected to hold an instance of this class referencing the shared link object. 
 */
@ApiModel(description = "Wraps an object URI defining a qualified association between two domain objects.  It is meant to be stored inline in an object denoting a unidirectional qualified reference. The association may reference a link object, which is a standalone persisted object, describing the qualities and participant of a multi-directional binding between aggregate roots. If such link object is present, all objects bound together by it are expected to hold an instance of this class referencing the shared link object. ")
@JsonPropertyOrder({
  RefObject.PLACEHOLDER,
  RefObject.DATA,
  RefObject.LINK,
  RefObject.REF
})
@JsonTypeName("RefObject")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class RefObject {
  public static final String PLACEHOLDER = "placeholder";
  private String placeholder;

  public static final String DATA = "data";
  private Map<String, Object> data = null;

  public static final String LINK = "link";
  private URI link;

  public static final String REF = "ref";
  private URI ref;

  public RefObject() { 
  }

  public RefObject placeholder(String placeholder) {
    
    this.placeholder = placeholder;
    return this;
  }

   /**
   * The textual representation of this association if the referenced domain object is missing, but the fact of the association existing is known. 
   * @return placeholder
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The textual representation of this association if the referenced domain object is missing, but the fact of the association existing is known. ")
  @JsonProperty(PLACEHOLDER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPlaceholder() {
    return placeholder;
  }


  @JsonProperty(PLACEHOLDER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }


  public RefObject data(Map<String, Object> data) {
    
    this.data = data;
    return this;
  }

  public RefObject putDataItem(String key, Object dataItem) {
    if (this.data == null) {
      this.data = new HashMap<>();
    }
    this.data.put(key, dataItem);
    return this;
  }

   /**
   * Arbitrary data qualifying this association from the perspective of the owner object, such as constraints and other metadata. 
   * @return data
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Arbitrary data qualifying this association from the perspective of the owner object, such as constraints and other metadata. ")
  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, Object> getData() {
    return data;
  }


  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setData(Map<String, Object> data) {
    this.data = data;
  }


  public RefObject link(URI link) {
    
    this.link = link;
    return this;
  }

   /**
   * Reference to the persisted binder object in case of a multi-directional association between multiple domain objects. If null, the association is unidirectional. 
   * @return link
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "Reference to the persisted binder object in case of a multi-directional association between multiple domain objects. If null, the association is unidirectional. ")
  @JsonProperty(LINK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getLink() {
    return link;
  }


  @JsonProperty(LINK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLink(URI link) {
    this.link = link;
  }


  public RefObject ref(URI ref) {
    
    this.ref = ref;
    return this;
  }

   /**
   * The unique persistence identifier of the associated object. 
   * @return ref
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The unique persistence identifier of the associated object. ")
  @JsonProperty(REF)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getRef() {
    return ref;
  }


  @JsonProperty(REF)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRef(URI ref) {
    this.ref = ref;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefObject refObject = (RefObject) o;
    return Objects.equals(this.placeholder, refObject.placeholder) &&
        Objects.equals(this.data, refObject.data) &&
        Objects.equals(this.link, refObject.link) &&
        Objects.equals(this.ref, refObject.ref);
  }

  @Override
  public int hashCode() {
    return Objects.hash(placeholder, data, link, ref);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefObject {\n");
    sb.append("    placeholder: ").append(toIndentedString(placeholder)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    link: ").append(toIndentedString(link)).append("\n");
    sb.append("    ref: ").append(toIndentedString(ref)).append("\n");
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

