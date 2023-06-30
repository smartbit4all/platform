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
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This definition object is a descriptor about the master data management in an application. Typically this is a single instance in the application but there can be more then one if we would like to manage separated set of data like in a multi tenant application. It is used as CollectionApi.reference to be able to identify by name. 
 */
@ApiModel(description = "This definition object is a descriptor about the master data management in an application. Typically this is a single instance in the application but there can be more then one if we would like to manage separated set of data like in a multi tenant application. It is used as CollectionApi.reference to be able to identify by name. ")
@JsonPropertyOrder({
  MDMDefinition.URI,
  MDMDefinition.NAME,
  MDMDefinition.ADMIN_GROUP_NAME,
  MDMDefinition.STATE,
  MDMDefinition.DESCRIPTORS
})
@JsonTypeName("MDMDefinition")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MDMDefinition {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String ADMIN_GROUP_NAME = "adminGroupName";
  private String adminGroupName;

  public static final String STATE = "state";
  private URI state;

  public static final String DESCRIPTORS = "descriptors";
  private Map<String, MDMEntryDescriptor> descriptors = new HashMap<>();

  public MDMDefinition() { 
  }

  public MDMDefinition uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The uri of the object.
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The uri of the object.")
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


  public MDMDefinition name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The logical name of the given mdm definition. This is also the default name of the schema.
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The logical name of the given mdm definition. This is also the default name of the schema.")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }


  public MDMDefinition adminGroupName(String adminGroupName) {
    
    this.adminGroupName = adminGroupName;
    return this;
  }

   /**
   * The name of the administration group. If a user is involved in the group then can adminiter all the entries inside the definition. The entries will have their own security group that will be included into this group. So be can manage all the entries one by one. It is mandatory to set this group name or else the master data management won&#39;t be able to setup the rights. 
   * @return adminGroupName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the administration group. If a user is involved in the group then can adminiter all the entries inside the definition. The entries will have their own security group that will be included into this group. So be can manage all the entries one by one. It is mandatory to set this group name or else the master data management won't be able to setup the rights. ")
  @JsonProperty(ADMIN_GROUP_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getAdminGroupName() {
    return adminGroupName;
  }


  @JsonProperty(ADMIN_GROUP_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAdminGroupName(String adminGroupName) {
    this.adminGroupName = adminGroupName;
  }


  public MDMDefinition state(URI state) {
    
    this.state = state;
    return this;
  }

   /**
   * The reference to the current state of the definition. It contains the necessary runtime informations.
   * @return state
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The reference to the current state of the definition. It contains the necessary runtime informations.")
  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getState() {
    return state;
  }


  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setState(URI state) {
    this.state = state;
  }


  public MDMDefinition descriptors(Map<String, MDMEntryDescriptor> descriptors) {
    
    this.descriptors = descriptors;
    return this;
  }

  public MDMDefinition putDescriptorsItem(String key, MDMEntryDescriptor descriptorsItem) {
    this.descriptors.put(key, descriptorsItem);
    return this;
  }

   /**
   * Get descriptors
   * @return descriptors
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(DESCRIPTORS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, MDMEntryDescriptor> getDescriptors() {
    return descriptors;
  }


  @JsonProperty(DESCRIPTORS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setDescriptors(Map<String, MDMEntryDescriptor> descriptors) {
    this.descriptors = descriptors;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MDMDefinition mdMDefinition = (MDMDefinition) o;
    return Objects.equals(this.uri, mdMDefinition.uri) &&
        Objects.equals(this.name, mdMDefinition.name) &&
        Objects.equals(this.adminGroupName, mdMDefinition.adminGroupName) &&
        Objects.equals(this.state, mdMDefinition.state) &&
        Objects.equals(this.descriptors, mdMDefinition.descriptors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, adminGroupName, state, descriptors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MDMDefinition {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    adminGroupName: ").append(toIndentedString(adminGroupName)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    descriptors: ").append(toIndentedString(descriptors)).append("\n");
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
