/*
 * View API
 * View API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.view.bean;

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
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * An anchor with a href representing a hyperlink object, which the client will open.  For possible attributes see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a 
 */
@ApiModel(description = "An anchor with a href representing a hyperlink object, which the client will open.  For possible attributes see https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a ")
@JsonPropertyOrder({
  Link.URL,
  Link.DOWNLOAD,
  Link.FILENAME,
  Link.TARGET
})
@JsonTypeName("Link")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class Link {
  public static final String URL = "url";
  private String url;

  public static final String DOWNLOAD = "download";
  private Boolean download;

  public static final String FILENAME = "filename";
  private String filename;

  /**
   * Gets or Sets target
   */
  public enum TargetEnum {
    SELF("SELF"),
    
    BLANK("BLANK"),
    
    PARENT("PARENT"),
    
    TOP("TOP");

    private String value;

    TargetEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TargetEnum fromValue(String value) {
      for (TargetEnum b : TargetEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String TARGET = "target";
  private TargetEnum target;

  public Link() { 
  }

  public Link url(String url) {
    
    this.url = url;
    return this;
  }

   /**
   * The URL that this link points to. It will be anchor&#39;s href attribute&#39;s value.
   * @return url
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The URL that this link points to. It will be anchor's href attribute's value.")
  @JsonProperty(URL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getUrl() {
    return url;
  }


  @JsonProperty(URL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUrl(String url) {
    this.url = url;
  }


  public Link download(Boolean download) {
    
    this.download = download;
    return this;
  }

   /**
   * Specifies if created anchor should contain download attribute.
   * @return download
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Specifies if created anchor should contain download attribute.")
  @JsonProperty(DOWNLOAD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getDownload() {
    return download;
  }


  @JsonProperty(DOWNLOAD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDownload(Boolean download) {
    this.download = download;
  }


  public Link filename(String filename) {
    
    this.filename = filename;
    return this;
  }

   /**
   * Used only when download is true, it will download attribute&#39;s value
   * @return filename
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Used only when download is true, it will download attribute's value")
  @JsonProperty(FILENAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFilename() {
    return filename;
  }


  @JsonProperty(FILENAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFilename(String filename) {
    this.filename = filename;
  }


  public Link target(TargetEnum target) {
    
    this.target = target;
    return this;
  }

   /**
   * Get target
   * @return target
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(TARGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public TargetEnum getTarget() {
    return target;
  }


  @JsonProperty(TARGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTarget(TargetEnum target) {
    this.target = target;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Link link = (Link) o;
    return Objects.equals(this.url, link.url) &&
        Objects.equals(this.download, link.download) &&
        Objects.equals(this.filename, link.filename) &&
        Objects.equals(this.target, link.target);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, download, filename, target);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Link {\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    download: ").append(toIndentedString(download)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    target: ").append(toIndentedString(target)).append("\n");
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

