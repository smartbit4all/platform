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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.view.bean.DownloadedFile;
import org.smartbit4all.api.view.bean.Link;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.View;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The same session can be valid / used in multiple UIs, this object represents a UI.
 */
@ApiModel(description = "The same session can be valid / used in multiple UIs, this object represents a UI.")
@JsonPropertyOrder({
  ViewContext.URI,
  ViewContext.UUID,
  ViewContext.VIEWS,
  ViewContext.LINKS,
  ViewContext.DOWNLOADS,
  ViewContext.OPEN_PENDING_DATA
})
@JsonTypeName("ViewContext")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ViewContext {
  public static final String URI = "uri";
  private URI uri;

  public static final String UUID = "uuid";
  private UUID uuid;

  public static final String VIEWS = "views";
  private List<View> views = new ArrayList<>();

  public static final String LINKS = "links";
  private List<Link> links = new ArrayList<>();

  public static final String DOWNLOADS = "downloads";
  private List<DownloadedFile> downloads = new ArrayList<>();

  public static final String OPEN_PENDING_DATA = "openPendingData";
  private OpenPendingData openPendingData;

  public ViewContext() { 
  }

  public ViewContext uri(URI uri) {
    
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


  public ViewContext uuid(UUID uuid) {
    
    this.uuid = uuid;
    return this;
  }

   /**
   * Get uuid
   * @return uuid
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(UUID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UUID getUuid() {
    return uuid;
  }


  @JsonProperty(UUID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }


  public ViewContext views(List<View> views) {
    
    this.views = views;
    return this;
  }

  public ViewContext addViewsItem(View viewsItem) {
    this.views.add(viewsItem);
    return this;
  }

   /**
   * Get views
   * @return views
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(VIEWS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<View> getViews() {
    return views;
  }


  @JsonProperty(VIEWS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setViews(List<View> views) {
    this.views = views;
  }


  public ViewContext links(List<Link> links) {
    
    this.links = links;
    return this;
  }

  public ViewContext addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

   /**
   * Get links
   * @return links
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(LINKS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<Link> getLinks() {
    return links;
  }


  @JsonProperty(LINKS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setLinks(List<Link> links) {
    this.links = links;
  }


  public ViewContext downloads(List<DownloadedFile> downloads) {
    
    this.downloads = downloads;
    return this;
  }

  public ViewContext addDownloadsItem(DownloadedFile downloadsItem) {
    this.downloads.add(downloadsItem);
    return this;
  }

   /**
   * Get downloads
   * @return downloads
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(DOWNLOADS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<DownloadedFile> getDownloads() {
    return downloads;
  }


  @JsonProperty(DOWNLOADS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setDownloads(List<DownloadedFile> downloads) {
    this.downloads = downloads;
  }


  public ViewContext openPendingData(OpenPendingData openPendingData) {
    
    this.openPendingData = openPendingData;
    return this;
  }

   /**
   * Get openPendingData
   * @return openPendingData
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OPEN_PENDING_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public OpenPendingData getOpenPendingData() {
    return openPendingData;
  }


  @JsonProperty(OPEN_PENDING_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOpenPendingData(OpenPendingData openPendingData) {
    this.openPendingData = openPendingData;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewContext viewContext = (ViewContext) o;
    return Objects.equals(this.uri, viewContext.uri) &&
        Objects.equals(this.uuid, viewContext.uuid) &&
        Objects.equals(this.views, viewContext.views) &&
        Objects.equals(this.links, viewContext.links) &&
        Objects.equals(this.downloads, viewContext.downloads) &&
        Objects.equals(this.openPendingData, viewContext.openPendingData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, uuid, views, links, downloads, openPendingData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewContext {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    views: ").append(toIndentedString(views)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    downloads: ").append(toIndentedString(downloads)).append("\n");
    sb.append("    openPendingData: ").append(toIndentedString(openPendingData)).append("\n");
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

