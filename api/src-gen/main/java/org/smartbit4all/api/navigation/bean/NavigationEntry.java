package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NavigationEntry
 */

public class NavigationEntry   {
  @JsonProperty("uri")
  private String uri;

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

  public NavigationEntry uri(String uri) {
    this.uri = uri;
    return this;
  }

  /**
   * The resource identifier of the given navigation entry. The format is: navigation://navigationpath#id where the navigation path is specific for the given navigation.
   * @return uri
  */
  @ApiModelProperty(required = true, value = "The resource identifier of the given navigation entry. The format is: navigation://navigationpath#id where the navigation path is specific for the given navigation.")
  @NotNull


  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
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
    return Objects.equals(this.uri, navigationEntry.uri) &&
        Objects.equals(this.name, navigationEntry.name) &&
        Objects.equals(this.icon, navigationEntry.icon) &&
        Objects.equals(this.styles, navigationEntry.styles) &&
        Objects.equals(this.views, navigationEntry.views);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, icon, styles, views);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationEntry {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
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

