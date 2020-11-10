package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NavigationView
 */

public class NavigationView   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("parameters")
  @Valid
  private Map<String, Object> parameters = null;

  public NavigationView name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The symbolic name of the view that must be supported by the given UI implementation.
   * @return name
  */
  @ApiModelProperty(required = true, value = "The symbolic name of the view that must be supported by the given UI implementation.")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public NavigationView parameters(Map<String, Object> parameters) {
    this.parameters = parameters;
    return this;
  }

  public NavigationView putParametersItem(String key, Object parametersItem) {
    if (this.parameters == null) {
      this.parameters = new HashMap<>();
    }
    this.parameters.put(key, parametersItem);
    return this;
  }

  /**
   * Get parameters
   * @return parameters
  */
  @ApiModelProperty(value = "")


  public Map<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationView navigationView = (NavigationView) o;
    return Objects.equals(this.name, navigationView.name) &&
        Objects.equals(this.parameters, navigationView.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationView {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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

