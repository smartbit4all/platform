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
import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewState;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * State update of a view.
 */
@ApiModel(description = "State update of a view.")
@JsonPropertyOrder({
  ViewStateUpdate.UUID,
  ViewStateUpdate.STATE
})
@JsonTypeName("ViewStateUpdate")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ViewStateUpdate {
  public static final String UUID = "uuid";
  private UUID uuid;

  public static final String STATE = "state";
  private ViewState state = ViewState.TO_OPEN;

  public ViewStateUpdate() { 
  }

  public ViewStateUpdate uuid(UUID uuid) {
    
    this.uuid = uuid;
    return this;
  }

   /**
   * ViewContext&#39;s unique identifier.
   * @return uuid
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "ViewContext's unique identifier.")
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


  public ViewStateUpdate state(ViewState state) {
    
    this.state = state;
    return this;
  }

   /**
   * Get state
   * @return state
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ViewState getState() {
    return state;
  }


  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setState(ViewState state) {
    this.state = state;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewStateUpdate viewStateUpdate = (ViewStateUpdate) o;
    return Objects.equals(this.uuid, viewStateUpdate.uuid) &&
        Objects.equals(this.state, viewStateUpdate.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewStateUpdate {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

