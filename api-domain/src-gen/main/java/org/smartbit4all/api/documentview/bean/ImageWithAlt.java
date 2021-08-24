/*
 * DocumentView API
 * DocumentView API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.documentview.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * ImageWithAlt
 */
@JsonPropertyOrder({
  ImageWithAlt.IMAGE,
  ImageWithAlt.ALT,
  ImageWithAlt.FILE_NAME
})
@JsonTypeName("ImageWithAlt")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ImageWithAlt {
  public static final String IMAGE = "image";
  private org.smartbit4all.types.binarydata.BinaryData image = null;

  public static final String ALT = "alt";
  private String alt;

  public static final String FILE_NAME = "fileName";
  private String fileName;


  public ImageWithAlt image(org.smartbit4all.types.binarydata.BinaryData image) {
    
    this.image = image;
    return this;
  }

   /**
   * Get image
   * @return image
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(IMAGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public org.smartbit4all.types.binarydata.BinaryData getImage() {
    return image;
  }


  @JsonProperty(IMAGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setImage(org.smartbit4all.types.binarydata.BinaryData image) {
    this.image = image;
  }


  public ImageWithAlt alt(String alt) {
    
    this.alt = alt;
    return this;
  }

   /**
   * Get alt
   * @return alt
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ALT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getAlt() {
    return alt;
  }


  @JsonProperty(ALT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAlt(String alt) {
    this.alt = alt;
  }


  public ImageWithAlt fileName(String fileName) {
    
    this.fileName = fileName;
    return this;
  }

   /**
   * Get fileName
   * @return fileName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(FILE_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFileName() {
    return fileName;
  }


  @JsonProperty(FILE_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImageWithAlt imageWithAlt = (ImageWithAlt) o;
    return Objects.equals(this.image, imageWithAlt.image) &&
        Objects.equals(this.alt, imageWithAlt.alt) &&
        Objects.equals(this.fileName, imageWithAlt.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(image, alt, fileName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImageWithAlt {\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
    sb.append("    alt: ").append(toIndentedString(alt)).append("\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
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

