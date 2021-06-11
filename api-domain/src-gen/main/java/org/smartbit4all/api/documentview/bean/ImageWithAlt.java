package org.smartbit4all.api.documentview.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ImageWithAlt
 */

public class ImageWithAlt   {
  @JsonProperty("image")
  private org.smartbit4all.types.binarydata.BinaryData image = null;

  @JsonProperty("alt")
  private String alt;

  @JsonProperty("fileName")
  private String fileName;

  public ImageWithAlt image(org.smartbit4all.types.binarydata.BinaryData image) {
    this.image = image;
    return this;
  }

  /**
   * Get image
   * @return image
  */
  @ApiModelProperty(value = "")

  @Valid

  public org.smartbit4all.types.binarydata.BinaryData getImage() {
    return image;
  }

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
  */
  @ApiModelProperty(value = "")


  public String getAlt() {
    return alt;
  }

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
  */
  @ApiModelProperty(value = "")


  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

