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
import org.smartbit4all.api.view.bean.ViewContext;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * ViewResponse
 */
@JsonPropertyOrder({
  ViewResponse.RESULT,
  ViewResponse.VIEW_CONTEXT
})
@JsonTypeName("ViewResponse")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ViewResponse {
  public static final String RESULT = "result";
  private Object result;

  public static final String VIEW_CONTEXT = "viewContext";
  private ViewContext viewContext;


  public ViewResponse result(Object result) {
    
    this.result = result;
    return this;
  }

   /**
   * Get result
   * @return result
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(RESULT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getResult() {
    return result;
  }


  @JsonProperty(RESULT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setResult(Object result) {
    this.result = result;
  }


  public ViewResponse viewContext(ViewContext viewContext) {
    
    this.viewContext = viewContext;
    return this;
  }

   /**
   * Get viewContext
   * @return viewContext
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VIEW_CONTEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ViewContext getViewContext() {
    return viewContext;
  }


  @JsonProperty(VIEW_CONTEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setViewContext(ViewContext viewContext) {
    this.viewContext = viewContext;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewResponse viewResponse = (ViewResponse) o;
    return Objects.equals(this.result, viewResponse.result) &&
        Objects.equals(this.viewContext, viewResponse.viewContext);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, viewContext);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewResponse {\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    viewContext: ").append(toIndentedString(viewContext)).append("\n");
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

