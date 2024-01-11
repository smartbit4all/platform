/*
 * Grid api
 * The grid api is resposible for the grid components that shows a list of item. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.grid.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.UiAction;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object is a grid row containening the identifier and actions releted with the row. 
 */
@ApiModel(description = "This object is a grid row containening the identifier and actions releted with the row. ")
@JsonPropertyOrder({
  GridRow.ID,
  GridRow.ACTIONS,
  GridRow.DATA,
  GridRow.SELECTABLE,
  GridRow.SELECTED,
  GridRow.ICONS,
  GridRow.PARENT,
  GridRow.CHILDREN
})
@JsonTypeName("GridRow")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GridRow {
  public static final String ID = "id";
  private String id;

  public static final String ACTIONS = "actions";
  private List<UiAction> actions = new ArrayList<>();

  public static final String DATA = "data";
  private Object data;

  public static final String SELECTABLE = "selectable";
  private Boolean selectable;

  public static final String SELECTED = "selected";
  private Boolean selected;

  public static final String ICONS = "icons";
  private Map<String, List<ImageResource>> icons = new HashMap<>();

  public static final String PARENT = "parent";
  private String parent;

  public static final String CHILDREN = "children";
  private List<String> children = null;

  public GridRow() { 
  }

  public GridRow id(String id) {
    
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getId() {
    return id;
  }


  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setId(String id) {
    this.id = id;
  }


  public GridRow actions(List<UiAction> actions) {
    
    this.actions = actions;
    return this;
  }

  public GridRow addActionsItem(UiAction actionsItem) {
    this.actions.add(actionsItem);
    return this;
  }

   /**
   * Get actions
   * @return actions
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(ACTIONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<UiAction> getActions() {
    return actions;
  }


  @JsonProperty(ACTIONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setActions(List<UiAction> actions) {
    this.actions = actions;
  }


  public GridRow data(Object data) {
    
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getData() {
    return data;
  }


  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setData(Object data) {
    this.data = data;
  }


  public GridRow selectable(Boolean selectable) {
    
    this.selectable = selectable;
    return this;
  }

   /**
   * Get selectable
   * @return selectable
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTABLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getSelectable() {
    return selectable;
  }


  @JsonProperty(SELECTABLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelectable(Boolean selectable) {
    this.selectable = selectable;
  }


  public GridRow selected(Boolean selected) {
    
    this.selected = selected;
    return this;
  }

   /**
   * Get selected
   * @return selected
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getSelected() {
    return selected;
  }


  @JsonProperty(SELECTED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelected(Boolean selected) {
    this.selected = selected;
  }


  public GridRow icons(Map<String, List<ImageResource>> icons) {
    
    this.icons = icons;
    return this;
  }

  public GridRow putIconsItem(String key, List<ImageResource> iconsItem) {
    this.icons.put(key, iconsItem);
    return this;
  }

   /**
   * Get icons
   * @return icons
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(ICONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, List<ImageResource>> getIcons() {
    return icons;
  }


  @JsonProperty(ICONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setIcons(Map<String, List<ImageResource>> icons) {
    this.icons = icons;
  }


  public GridRow parent(String parent) {
    
    this.parent = parent;
    return this;
  }

   /**
   * If the grid can be hierarchical then the parent contains the identifiers of the parent row. 
   * @return parent
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If the grid can be hierarchical then the parent contains the identifiers of the parent row. ")
  @JsonProperty(PARENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getParent() {
    return parent;
  }


  @JsonProperty(PARENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setParent(String parent) {
    this.parent = parent;
  }


  public GridRow children(List<String> children) {
    
    this.children = children;
    return this;
  }

  public GridRow addChildrenItem(String childrenItem) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(childrenItem);
    return this;
  }

   /**
   * If the grid can be hierarchical then the children list contains the identifiers of the children rows. The rows are not necessarily included in the page but the client can ask for this. The children is not required to see if it is empty or not set. 
   * @return children
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If the grid can be hierarchical then the children list contains the identifiers of the children rows. The rows are not necessarily included in the page but the client can ask for this. The children is not required to see if it is empty or not set. ")
  @JsonProperty(CHILDREN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getChildren() {
    return children;
  }


  @JsonProperty(CHILDREN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setChildren(List<String> children) {
    this.children = children;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GridRow gridRow = (GridRow) o;
    return Objects.equals(this.id, gridRow.id) &&
        Objects.equals(this.actions, gridRow.actions) &&
        Objects.equals(this.data, gridRow.data) &&
        Objects.equals(this.selectable, gridRow.selectable) &&
        Objects.equals(this.selected, gridRow.selected) &&
        Objects.equals(this.icons, gridRow.icons) &&
        Objects.equals(this.parent, gridRow.parent) &&
        Objects.equals(this.children, gridRow.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, actions, data, selectable, selected, icons, parent, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridRow {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    actions: ").append(toIndentedString(actions)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    selectable: ").append(toIndentedString(selectable)).append("\n");
    sb.append("    selected: ").append(toIndentedString(selected)).append("\n");
    sb.append("    icons: ").append(toIndentedString(icons)).append("\n");
    sb.append("    parent: ").append(toIndentedString(parent)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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

