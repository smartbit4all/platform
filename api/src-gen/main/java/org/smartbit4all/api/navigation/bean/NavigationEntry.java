package org.smartbit4all.api.navigation.bean;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * NavigationEntry
 */

public class NavigationEntry   {
  @JsonProperty("objectUri")
  private URI objectUri;

  @JsonProperty("metaUri")
  private URI metaUri;

  @JsonProperty("name")
  private String name;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("styles")
  @Valid
  private List<String> styles = null;

  @JsonProperty("views")
  @Valid
  private List<NavigationView> views = null;

  public NavigationEntry objectUri(URI objectUri) {
    this.objectUri = objectUri;
    return this;
  }

  /**
   * The URI identifies the so called API object.
   * @return objectUri
  */
  @ApiModelProperty(value = "The URI identifies the so called API object.")

  @Valid

  public URI getObjectUri() {
    return objectUri;
  }

  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }

  public NavigationEntry metaUri(URI metaUri) {
    this.metaUri = metaUri;
    return this;
  }

  /**
   * The URI identifies the meta object of the entry
   * @return metaUri
  */
  @ApiModelProperty(value = "The URI identifies the meta object of the entry")

  @Valid

  public URI getMetaUri() {
    return metaUri;
  }

  public void setMetaUri(URI metaUri) {
    this.metaUri = metaUri;
  }

  public NavigationEntry name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the entry
   * @return name
  */
  @ApiModelProperty(required = true, value = "The name of the entry")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public NavigationEntry icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * The name of the icon resource
   * @return icon
  */
  @ApiModelProperty(value = "The name of the icon resource")


  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public NavigationEntry styles(List<String> styles) {
    this.styles = styles;
    return this;
  }

  public NavigationEntry addStylesItem(String stylesItem) {
    if (this.styles == null) {
      this.styles = new ArrayList<>();
    }
    this.styles.add(stylesItem);
    return this;
  }

  /**
   * The string list of the abstract styles that can be applied to the given entry
   * @return styles
  */
  @ApiModelProperty(value = "The string list of the abstract styles that can be applied to the given entry")


  public List<String> getStyles() {
    return styles;
  }

  public void setStyles(List<String> styles) {
    this.styles = styles;
  }

  public NavigationEntry views(List<NavigationView> views) {
    this.views = views;
    return this;
  }

  public NavigationEntry addViewsItem(NavigationView viewsItem) {
    if (this.views == null) {
      this.views = new ArrayList<>();
    }
    this.views.add(viewsItem);
    return this;
  }

  /**
   * The string list of the abstract views that are supported by the UI that we have. This view will get the navigation entry as parameter.
   * @return views
  */
  @ApiModelProperty(value = "The string list of the abstract views that are supported by the UI that we have. This view will get the navigation entry as parameter.")

  @Valid

  public List<NavigationView> getViews() {
    return views;
  }

  public void setViews(List<NavigationView> views) {
    this.views = views;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationEntry navigationEntry = (NavigationEntry) o;
    return Objects.equals(this.objectUri, navigationEntry.objectUri) &&
        Objects.equals(this.metaUri, navigationEntry.metaUri) &&
        Objects.equals(this.name, navigationEntry.name) &&
        Objects.equals(this.icon, navigationEntry.icon) &&
        Objects.equals(this.styles, navigationEntry.styles) &&
        Objects.equals(this.views, navigationEntry.views);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUri, metaUri, name, icon, styles, views);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationEntry {\n");
    
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
    sb.append("    metaUri: ").append(toIndentedString(metaUri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    styles: ").append(toIndentedString(styles)).append("\n");
    sb.append("    views: ").append(toIndentedString(views)).append("\n");
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

