package org.smartbit4all.ui.common.action;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Action
 */

public class Action {
  private String name; 

  private String label;

  private String iconCode;

  private String kind;
  
  private List<URI> containers;
  
  public void execute() {
  }
  
  public Action() {
    super();
  }
  
  public Action(String name) {
    super();
    this.name = name;
  }

  /**
   * The name of the action that is displayed.
   * 
   * @return name
   */
  public String getName() {
    return name;
  }

  public Action label(String label) {
    this.label = label;
    return this;
  }

  /**
   * The name of the action that is displayed.
   * 
   * @return name
   */
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Action iconCode(String iconCode) {
    this.iconCode = iconCode;
    return this;
  }

  /**
   * The icon code.
   * 
   * @return iconCode
   */
  public String getIconCode() {
    return iconCode;
  }

  public void setIconCode(String iconCode) {
    this.iconCode = iconCode;
  }

  public Action kind(String kind) {
    this.kind = kind;
    return this;
  }

  /**
   * The kind of the action.
   * 
   * @return kind
   */
  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }
  

  public Action containers(List<URI> containers) {
    this.containers = containers;
    return this;
  }

  /**
   * The name of the action that is displayed.
   * 
   * @return name
   */
  public List<URI> getContainers() {
    return containers;
  }

  public void setContainers(List<URI> containers) {
    this.containers = containers;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Action Action = (Action) o;
    return Objects.equals(this.label, Action.label)
        && Objects.equals(this.iconCode, Action.iconCode)
        && Objects.equals(this.kind, Action.kind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, iconCode, kind);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Action {\n");

    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    iconCode: ").append(toIndentedString(iconCode)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

