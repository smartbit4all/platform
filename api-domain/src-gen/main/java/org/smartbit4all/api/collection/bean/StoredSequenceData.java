/*
 * collection api
 * collection api for the stored colections.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.collection.bean;

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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object contains an atomic sequence that provides globaly unique incrementing value. Can be used as the classic database sequence. 
 */
@ApiModel(description = "This object contains an atomic sequence that provides globaly unique incrementing value. Can be used as the classic database sequence. ")
@JsonPropertyOrder({
  StoredSequenceData.URI,
  StoredSequenceData.NAME,
  StoredSequenceData.CURRENT
})
@JsonTypeName("StoredSequenceData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class StoredSequenceData {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String CURRENT = "current";
  private Long current;

  public StoredSequenceData() { 
  }

  public StoredSequenceData uri(URI uri) {
    
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


  public StoredSequenceData name(String name) {
    
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


  public StoredSequenceData current(Long current) {
    
    this.current = current;
    return this;
  }

   /**
   * The current value of the sequence.
   * @return current
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The current value of the sequence.")
  @JsonProperty(CURRENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getCurrent() {
    return current;
  }


  @JsonProperty(CURRENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCurrent(Long current) {
    this.current = current;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StoredSequenceData storedSequenceData = (StoredSequenceData) o;
    return Objects.equals(this.uri, storedSequenceData.uri) &&
        Objects.equals(this.name, storedSequenceData.name) &&
        Objects.equals(this.current, storedSequenceData.current);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, current);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StoredSequenceData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    current: ").append(toIndentedString(current)).append("\n");
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

