/*
 * smartbit4all navigation api
 * smartbit4all navigation api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.navigation.bean;

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
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * API: Describes all the entries and associations in the given navigation.
 */
@ApiModel(description = "API: Describes all the entries and associations in the given navigation.")
@JsonPropertyOrder({
  NavigationConfig.ENTRIES,
  NavigationConfig.ASSOCIATIONS
})
@JsonTypeName("NavigationConfig")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class NavigationConfig {
  public static final String ENTRIES = "entries";
  private List<NavigationEntryMeta> entries = null;

  public static final String ASSOCIATIONS = "associations";
  private List<NavigationAssociationMeta> associations = null;

  public NavigationConfig() { 
  }

  public NavigationConfig entries(List<NavigationEntryMeta> entries) {
    
    this.entries = entries;
    return this;
  }

  public NavigationConfig addEntriesItem(NavigationEntryMeta entriesItem) {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    this.entries.add(entriesItem);
    return this;
  }

   /**
   * The available entries in the given navigation config.
   * @return entries
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The available entries in the given navigation config.")
  @JsonProperty(ENTRIES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<NavigationEntryMeta> getEntries() {
    return entries;
  }


  @JsonProperty(ENTRIES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEntries(List<NavigationEntryMeta> entries) {
    this.entries = entries;
  }


  public NavigationConfig associations(List<NavigationAssociationMeta> associations) {
    
    this.associations = associations;
    return this;
  }

  public NavigationConfig addAssociationsItem(NavigationAssociationMeta associationsItem) {
    if (this.associations == null) {
      this.associations = new ArrayList<>();
    }
    this.associations.add(associationsItem);
    return this;
  }

   /**
   * The navigable associations in the given navigation. In case of a navigation tree these are the openable sub trees.
   * @return associations
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The navigable associations in the given navigation. In case of a navigation tree these are the openable sub trees.")
  @JsonProperty(ASSOCIATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<NavigationAssociationMeta> getAssociations() {
    return associations;
  }


  @JsonProperty(ASSOCIATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAssociations(List<NavigationAssociationMeta> associations) {
    this.associations = associations;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationConfig navigationConfig = (NavigationConfig) o;
    return Objects.equals(this.entries, navigationConfig.entries) &&
        Objects.equals(this.associations, navigationConfig.associations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries, associations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationConfig {\n");
    sb.append("    entries: ").append(toIndentedString(entries)).append("\n");
    sb.append("    associations: ").append(toIndentedString(associations)).append("\n");
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

