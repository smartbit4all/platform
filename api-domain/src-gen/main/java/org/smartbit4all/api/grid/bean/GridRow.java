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
import java.util.List;
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
  GridRow.DATA
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
        Objects.equals(this.data, gridRow.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, actions, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridRow {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    actions: ").append(toIndentedString(actions)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

