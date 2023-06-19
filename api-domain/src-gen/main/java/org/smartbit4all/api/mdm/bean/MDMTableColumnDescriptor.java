/*
 * MasterDataManagement api
 * null
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.mdm.bean;

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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This definition object is a descriptor about the master data management in an application. Typically this is a single instance in the application but there can be more then one if we would like to manage separated set of data like in a multi tenant application. It is used as CollectionApi.reference to be able to identify by name. 
 */
@ApiModel(description = "This definition object is a descriptor about the master data management in an application. Typically this is a single instance in the application but there can be more then one if we would like to manage separated set of data like in a multi tenant application. It is used as CollectionApi.reference to be able to identify by name. ")
@JsonPropertyOrder({
  MDMTableColumnDescriptor.NAME,
  MDMTableColumnDescriptor.PATH
})
@JsonTypeName("MDMTableColumnDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MDMTableColumnDescriptor {
  public static final String NAME = "name";
  private String name;

  public static final String PATH = "path";
  private List<String> path = null;

  public MDMTableColumnDescriptor() { 
  }

  public MDMTableColumnDescriptor name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The name of the given column.
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The name of the given column.")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setName(String name) {
    this.name = name;
  }


  public MDMTableColumnDescriptor path(List<String> path) {
    
    this.path = path;
    return this;
  }

  public MDMTableColumnDescriptor addPathItem(String pathItem) {
    if (this.path == null) {
      this.path = new ArrayList<>();
    }
    this.path.add(pathItem);
    return this;
  }

   /**
   * The path to access the object property of the column.
   * @return path
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The path to access the object property of the column.")
  @JsonProperty(PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getPath() {
    return path;
  }


  @JsonProperty(PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPath(List<String> path) {
    this.path = path;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MDMTableColumnDescriptor mdMTableColumnDescriptor = (MDMTableColumnDescriptor) o;
    return Objects.equals(this.name, mdMTableColumnDescriptor.name) &&
        Objects.equals(this.path, mdMTableColumnDescriptor.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, path);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MDMTableColumnDescriptor {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
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

