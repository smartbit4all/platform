package org.smartbit4all.api.navigation.bean;

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
  @JsonProperty("id")
  private String id;

  @JsonProperty("correlationId")
  private String correlationId;

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

  @JsonProperty("children")
  @Valid
  private List<NavigationEntry> children = null;

  @JsonProperty("parent")
  private NavigationEntry parent;

  public NavigationEntry id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the given entry - UUID
   * @return id
  */
  @ApiModelProperty(value = "The unique identifier of the given entry - UUID")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NavigationEntry correlationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  /**
   * The navigation entry refers a given resource. Like entity, docuemnt, folder or anything else. This resourse for the given API is identified by this id.
   * @return correlationId
  */
  @ApiModelProperty(value = "The navigation entry refers a given resource. Like entity, docuemnt, folder or anything else. This resourse for the given API is identified by this id.")


  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
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

  public NavigationEntry children(List<NavigationEntry> children) {
    this.children = children;
    return this;
  }

  public NavigationEntry addChildrenItem(NavigationEntry childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(childrenItem);
    return this;
  }

  /**
   * The children of the current entry.
   * @return children
  */
  @ApiModelProperty(value = "The children of the current entry.")

  @Valid

  public List<NavigationEntry> getChildren() {
    return children;
  }

  public void setChildren(List<NavigationEntry> children) {
    this.children = children;
  }

  public NavigationEntry parent(NavigationEntry parent) {
    this.parent = parent;
    return this;
  }

  /**
   * Get parent
   * @return parent
  */
  @ApiModelProperty(value = "")

  @Valid

  public NavigationEntry getParent() {
    return parent;
  }

  public void setParent(NavigationEntry parent) {
    this.parent = parent;
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
    return Objects.equals(this.id, navigationEntry.id) &&
        Objects.equals(this.correlationId, navigationEntry.correlationId) &&
        Objects.equals(this.name, navigationEntry.name) &&
        Objects.equals(this.icon, navigationEntry.icon) &&
        Objects.equals(this.styles, navigationEntry.styles) &&
        Objects.equals(this.views, navigationEntry.views) &&
        Objects.equals(this.children, navigationEntry.children) &&
        Objects.equals(this.parent, navigationEntry.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, correlationId, name, icon, styles, views, children, parent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationEntry {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    correlationId: ").append(toIndentedString(correlationId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    styles: ").append(toIndentedString(styles)).append("\n");
    sb.append("    views: ").append(toIndentedString(views)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    parent: ").append(toIndentedString(parent)).append("\n");
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

