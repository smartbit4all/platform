/*
 * Object api
 * The object api responsible for the domain object meta information including the object definitions and the relations among them. These objects are stored because the modules can contribute. The modules have their own ObjectApi that manages the storage and ensure the up-to-date view of the current data. The algorithms are running on the ObjectApi cache refreshed periodically. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.object.bean;

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
 * This object is a table data row containening the identifier and actions releted with the row. 
 */
@ApiModel(description = "This object is a table data row containening the identifier and actions releted with the row. ")
@JsonPropertyOrder({
  TableDataContentRow.ID,
  TableDataContentRow.ACTIONS,
  TableDataContentRow.DATA
})
@JsonTypeName("TableDataContentRow")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class TableDataContentRow {
  public static final String ID = "id";
  private String id;

  public static final String ACTIONS = "actions";
  private List<UiAction> actions = new ArrayList<>();

  public static final String DATA = "data";
  private Object data;

  public TableDataContentRow() { 
  }

  public TableDataContentRow id(String id) {
    
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


  public TableDataContentRow actions(List<UiAction> actions) {
    
    this.actions = actions;
    return this;
  }

  public TableDataContentRow addActionsItem(UiAction actionsItem) {
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


  public TableDataContentRow data(Object data) {
    
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
    TableDataContentRow tableDataContentRow = (TableDataContentRow) o;
    return Objects.equals(this.id, tableDataContentRow.id) &&
        Objects.equals(this.actions, tableDataContentRow.actions) &&
        Objects.equals(this.data, tableDataContentRow.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, actions, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TableDataContentRow {\n");
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

