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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.view.bean.CloseResult;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * All information regarding opening a view.
 */
@ApiModel(description = "All information regarding opening a view.")
@JsonPropertyOrder({
  OpenPendingData.VIEW_TO_OPEN,
  OpenPendingData.VIEWS_TO_CLOSE,
  OpenPendingData.RESULTS
})
@JsonTypeName("OpenPendingData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class OpenPendingData {
  public static final String VIEW_TO_OPEN = "viewToOpen";
  private UUID viewToOpen;

  public static final String VIEWS_TO_CLOSE = "viewsToClose";
  private List<UUID> viewsToClose = null;

  public static final String RESULTS = "results";
  private Map<String, CloseResult> results = new HashMap<>();

  public OpenPendingData() { 
  }

  public OpenPendingData viewToOpen(UUID viewToOpen) {
    
    this.viewToOpen = viewToOpen;
    return this;
  }

   /**
   * Get viewToOpen
   * @return viewToOpen
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VIEW_TO_OPEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UUID getViewToOpen() {
    return viewToOpen;
  }


  @JsonProperty(VIEW_TO_OPEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setViewToOpen(UUID viewToOpen) {
    this.viewToOpen = viewToOpen;
  }


  public OpenPendingData viewsToClose(List<UUID> viewsToClose) {
    
    this.viewsToClose = viewsToClose;
    return this;
  }

  public OpenPendingData addViewsToCloseItem(UUID viewsToCloseItem) {
    if (this.viewsToClose == null) {
      this.viewsToClose = new ArrayList<>();
    }
    this.viewsToClose.add(viewsToCloseItem);
    return this;
  }

   /**
   * Get viewsToClose
   * @return viewsToClose
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VIEWS_TO_CLOSE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<UUID> getViewsToClose() {
    return viewsToClose;
  }


  @JsonProperty(VIEWS_TO_CLOSE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setViewsToClose(List<UUID> viewsToClose) {
    this.viewsToClose = viewsToClose;
  }


  public OpenPendingData results(Map<String, CloseResult> results) {
    
    this.results = results;
    return this;
  }

  public OpenPendingData putResultsItem(String key, CloseResult resultsItem) {
    this.results.put(key, resultsItem);
    return this;
  }

   /**
   * Get results
   * @return results
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(RESULTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, CloseResult> getResults() {
    return results;
  }


  @JsonProperty(RESULTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setResults(Map<String, CloseResult> results) {
    this.results = results;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenPendingData openPendingData = (OpenPendingData) o;
    return Objects.equals(this.viewToOpen, openPendingData.viewToOpen) &&
        Objects.equals(this.viewsToClose, openPendingData.viewsToClose) &&
        Objects.equals(this.results, openPendingData.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(viewToOpen, viewsToClose, results);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenPendingData {\n");
    sb.append("    viewToOpen: ").append(toIndentedString(viewToOpen)).append("\n");
    sb.append("    viewsToClose: ").append(toIndentedString(viewsToClose)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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

