/*
 * smartbit4all navigation api
 * smartbit4all navigation api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.navigation.bean;

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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object can hold a navigation path in a navigation. The path must start with an entry object uri. This uri must be available as root or child of another node. The next path segment is an association and an entry uri again. This can be used for selection and using these path we can restore the state of a navigation. 
 */
@ApiModel(description = "This object can hold a navigation path in a navigation. The path must start with an entry object uri. This uri must be available as root or child of another node. The next path segment is an association and an entry uri again. This can be used for selection and using these path we can restore the state of a navigation. ")
@JsonPropertyOrder({
  NavigationPath.URI,
  NavigationPath.SEGMENTS
})
@JsonTypeName("NavigationPath")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class NavigationPath {
  public static final String URI = "uri";
  private URI uri;

  public static final String SEGMENTS = "segments";
  private List<String> segments = null;


  public NavigationPath uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The navigation can be saved using this.
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The navigation can be saved using this.")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public NavigationPath segments(List<String> segments) {
    
    this.segments = segments;
    return this;
  }

  public NavigationPath addSegmentsItem(String segmentsItem) {
    if (this.segments == null) {
      this.segments = new ArrayList<>();
    }
    this.segments.add(segmentsItem);
    return this;
  }

   /**
   * Get segments
   * @return segments
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SEGMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getSegments() {
    return segments;
  }


  @JsonProperty(SEGMENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSegments(List<String> segments) {
    this.segments = segments;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationPath navigationPath = (NavigationPath) o;
    return Objects.equals(this.uri, navigationPath.uri) &&
        Objects.equals(this.segments, navigationPath.segments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, segments);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationPath {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    segments: ").append(toIndentedString(segments)).append("\n");
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

