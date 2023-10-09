/*
 * org api
 * org api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.org.bean;

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
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * OrgState
 */
@JsonPropertyOrder({
  OrgState.URI,
  OrgState.USERS,
  OrgState.GROUPS,
  OrgState.USERS_OF_GROUP,
  OrgState.GROUPS_OF_GROUP
})
@JsonTypeName("OrgState")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class OrgState {
  public static final String URI = "uri";
  private URI uri;

  public static final String USERS = "users";
  private Map<String, User> users = new HashMap<>();

  public static final String GROUPS = "groups";
  private Map<String, Group> groups = new HashMap<>();

  public static final String USERS_OF_GROUP = "usersOfGroup";
  private Map<String, List<User>> usersOfGroup = new HashMap<>();

  public static final String GROUPS_OF_GROUP = "groupsOfGroup";
  private Map<String, List<Group>> groupsOfGroup = new HashMap<>();

  public OrgState() { 
  }

  public OrgState uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public OrgState users(Map<String, User> users) {
    
    this.users = users;
    return this;
  }

  public OrgState putUsersItem(String key, User usersItem) {
    this.users.put(key, usersItem);
    return this;
  }

   /**
   * Get users
   * @return users
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(USERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, User> getUsers() {
    return users;
  }


  @JsonProperty(USERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUsers(Map<String, User> users) {
    this.users = users;
  }


  public OrgState groups(Map<String, Group> groups) {
    
    this.groups = groups;
    return this;
  }

  public OrgState putGroupsItem(String key, Group groupsItem) {
    this.groups.put(key, groupsItem);
    return this;
  }

   /**
   * Get groups
   * @return groups
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(GROUPS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, Group> getGroups() {
    return groups;
  }


  @JsonProperty(GROUPS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setGroups(Map<String, Group> groups) {
    this.groups = groups;
  }


  public OrgState usersOfGroup(Map<String, List<User>> usersOfGroup) {
    
    this.usersOfGroup = usersOfGroup;
    return this;
  }

  public OrgState putUsersOfGroupItem(String key, List<User> usersOfGroupItem) {
    this.usersOfGroup.put(key, usersOfGroupItem);
    return this;
  }

   /**
   * Get usersOfGroup
   * @return usersOfGroup
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(USERS_OF_GROUP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, List<User>> getUsersOfGroup() {
    return usersOfGroup;
  }


  @JsonProperty(USERS_OF_GROUP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUsersOfGroup(Map<String, List<User>> usersOfGroup) {
    this.usersOfGroup = usersOfGroup;
  }


  public OrgState groupsOfGroup(Map<String, List<Group>> groupsOfGroup) {
    
    this.groupsOfGroup = groupsOfGroup;
    return this;
  }

  public OrgState putGroupsOfGroupItem(String key, List<Group> groupsOfGroupItem) {
    this.groupsOfGroup.put(key, groupsOfGroupItem);
    return this;
  }

   /**
   * Get groupsOfGroup
   * @return groupsOfGroup
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(GROUPS_OF_GROUP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, List<Group>> getGroupsOfGroup() {
    return groupsOfGroup;
  }


  @JsonProperty(GROUPS_OF_GROUP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setGroupsOfGroup(Map<String, List<Group>> groupsOfGroup) {
    this.groupsOfGroup = groupsOfGroup;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrgState orgState = (OrgState) o;
    return Objects.equals(this.uri, orgState.uri) &&
        Objects.equals(this.users, orgState.users) &&
        Objects.equals(this.groups, orgState.groups) &&
        Objects.equals(this.usersOfGroup, orgState.usersOfGroup) &&
        Objects.equals(this.groupsOfGroup, orgState.groupsOfGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, users, groups, usersOfGroup, groupsOfGroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrgState {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    usersOfGroup: ").append(toIndentedString(usersOfGroup)).append("\n");
    sb.append("    groupsOfGroup: ").append(toIndentedString(groupsOfGroup)).append("\n");
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
