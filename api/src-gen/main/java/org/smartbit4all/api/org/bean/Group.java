package org.smartbit4all.api.org.bean;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Group
 */

public class Group   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("kind")
  private URI kind;

  @JsonProperty("parent")
  private URI parent;

  @JsonProperty("children")
  @Valid
  private List<URI> children = null;

  public Group uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * The uri of the group
   * @return uri
  */
  @ApiModelProperty(required = true, value = "The uri of the group")
  @NotNull

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public Group name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the group
   * @return name
  */
  @ApiModelProperty(required = true, value = "The name of the group")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Group description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the group
   * @return description
  */
  @ApiModelProperty(value = "The description of the group")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Group kind(URI kind) {
    this.kind = kind;
    return this;
  }

  /**
   * The uri of the kind of the group. Eg.: role, organization, team etc.
   * @return kind
  */
  @ApiModelProperty(required = true, value = "The uri of the kind of the group. Eg.: role, organization, team etc.")
  @NotNull

  @Valid

  public URI getKind() {
    return kind;
  }

  public void setKind(URI kind) {
    this.kind = kind;
  }

  public Group parent(URI parent) {
    this.parent = parent;
    return this;
  }

  /**
   * The uri of the parent group
   * @return parent
  */
  @ApiModelProperty(value = "The uri of the parent group")

  @Valid

  public URI getParent() {
    return parent;
  }

  public void setParent(URI parent) {
    this.parent = parent;
  }

  public Group children(List<URI> children) {
    this.children = children;
    return this;
  }

  public Group addChildrenItem(URI childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(childrenItem);
    return this;
  }

  /**
   * The uris of the child groups
   * @return children
  */
  @ApiModelProperty(value = "The uris of the child groups")

  @Valid

  public List<URI> getChildren() {
    return children;
  }

  public void setChildren(List<URI> children) {
    this.children = children;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Group group = (Group) o;
    return Objects.equals(this.uri, group.uri) &&
        Objects.equals(this.name, group.name) &&
        Objects.equals(this.description, group.description) &&
        Objects.equals(this.kind, group.kind) &&
        Objects.equals(this.parent, group.parent) &&
        Objects.equals(this.children, group.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, description, kind, parent, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Group {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    parent: ").append(toIndentedString(parent)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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

