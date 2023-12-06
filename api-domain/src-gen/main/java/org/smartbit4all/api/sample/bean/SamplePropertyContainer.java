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
import org.smartbit4all.api.sample.bean.SampleProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * SamplePropertyContainer
 */
@JsonPropertyOrder({
  SamplePropertyContainer.NAME,
  SamplePropertyContainer.PROPS
})
@JsonTypeName("SamplePropertyContainer")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SamplePropertyContainer {
  public static final String NAME = "name";
  private String name;

  public static final String PROPS = "props";
  private SampleProperties props;

  public SamplePropertyContainer() { 
  }

  public SamplePropertyContainer name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }


  public SamplePropertyContainer props(SampleProperties props) {
    
    this.props = props;
    return this;
  }

   /**
   * Get props
   * @return props
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(PROPS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SampleProperties getProps() {
    return props;
  }


  @JsonProperty(PROPS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setProps(SampleProperties props) {
    this.props = props;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SamplePropertyContainer samplePropertyContainer = (SamplePropertyContainer) o;
    return Objects.equals(this.name, samplePropertyContainer.name) &&
        Objects.equals(this.props, samplePropertyContainer.props);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, props);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SamplePropertyContainer {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    props: ").append(toIndentedString(props)).append("\n");
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

