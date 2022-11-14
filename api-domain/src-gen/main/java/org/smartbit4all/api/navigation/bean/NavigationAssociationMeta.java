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
import java.net.URI;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * API: The association meta describes a possible navigation between entry metas. It is part of the configuration.
 */
@ApiModel(description = "API: The association meta describes a possible navigation between entry metas. It is part of the configuration.")
@JsonPropertyOrder({
  NavigationAssociationMeta.URI,
  NavigationAssociationMeta.NAME,
  NavigationAssociationMeta.START_ENTRY,
  NavigationAssociationMeta.END_ENTRY,
  NavigationAssociationMeta.ASSOCIATION_ENTRY
})
@JsonTypeName("NavigationAssociationMeta")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class NavigationAssociationMeta {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String START_ENTRY = "startEntry";
  private NavigationEntryMeta startEntry;

  public static final String END_ENTRY = "endEntry";
  private NavigationEntryMeta endEntry;

  public static final String ASSOCIATION_ENTRY = "associationEntry";
  private NavigationEntryMeta associationEntry;

  public NavigationAssociationMeta() { 
  }

  public NavigationAssociationMeta uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The uri of the resource that uniquely identifies the given navigation inside the navigation api.
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The uri of the resource that uniquely identifies the given navigation inside the navigation api.")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public NavigationAssociationMeta name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The well-formed name of the association.
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The well-formed name of the association.")
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


  public NavigationAssociationMeta startEntry(NavigationEntryMeta startEntry) {
    
    this.startEntry = startEntry;
    return this;
  }

   /**
   * Get startEntry
   * @return startEntry
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(START_ENTRY)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NavigationEntryMeta getStartEntry() {
    return startEntry;
  }


  @JsonProperty(START_ENTRY)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setStartEntry(NavigationEntryMeta startEntry) {
    this.startEntry = startEntry;
  }


  public NavigationAssociationMeta endEntry(NavigationEntryMeta endEntry) {
    
    this.endEntry = endEntry;
    return this;
  }

   /**
   * Get endEntry
   * @return endEntry
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(END_ENTRY)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NavigationEntryMeta getEndEntry() {
    return endEntry;
  }


  @JsonProperty(END_ENTRY)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setEndEntry(NavigationEntryMeta endEntry) {
    this.endEntry = endEntry;
  }


  public NavigationAssociationMeta associationEntry(NavigationEntryMeta associationEntry) {
    
    this.associationEntry = associationEntry;
    return this;
  }

   /**
   * Get associationEntry
   * @return associationEntry
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ASSOCIATION_ENTRY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public NavigationEntryMeta getAssociationEntry() {
    return associationEntry;
  }


  @JsonProperty(ASSOCIATION_ENTRY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAssociationEntry(NavigationEntryMeta associationEntry) {
    this.associationEntry = associationEntry;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationAssociationMeta navigationAssociationMeta = (NavigationAssociationMeta) o;
    return Objects.equals(this.uri, navigationAssociationMeta.uri) &&
        Objects.equals(this.name, navigationAssociationMeta.name) &&
        Objects.equals(this.startEntry, navigationAssociationMeta.startEntry) &&
        Objects.equals(this.endEntry, navigationAssociationMeta.endEntry) &&
        Objects.equals(this.associationEntry, navigationAssociationMeta.associationEntry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, startEntry, endEntry, associationEntry);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationAssociationMeta {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    startEntry: ").append(toIndentedString(startEntry)).append("\n");
    sb.append("    endEntry: ").append(toIndentedString(endEntry)).append("\n");
    sb.append("    associationEntry: ").append(toIndentedString(associationEntry)).append("\n");
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

