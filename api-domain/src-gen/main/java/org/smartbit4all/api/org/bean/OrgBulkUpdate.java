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
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.org.bean.GroupOfGroupUpdate;
import org.smartbit4all.api.org.bean.GroupUpdate;
import org.smartbit4all.api.org.bean.UserOfGroupUpdate;
import org.smartbit4all.api.org.bean.UserUpdate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * OrgBulkUpdate
 */
@JsonPropertyOrder({
  OrgBulkUpdate.URI,
  OrgBulkUpdate.USERS,
  OrgBulkUpdate.GROUPS,
  OrgBulkUpdate.GROUPS_OF_GROUP,
  OrgBulkUpdate.USERS_OF_GROUP
})
@JsonTypeName("OrgBulkUpdate")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class OrgBulkUpdate {
  public static final String URI = "uri";
  private URI uri;

  public static final String USERS = "users";
  private List<UserUpdate> users = new ArrayList<>();

  public static final String GROUPS = "groups";
  private List<GroupUpdate> groups = new ArrayList<>();

  public static final String GROUPS_OF_GROUP = "groupsOfGroup";
  private List<GroupOfGroupUpdate> groupsOfGroup = new ArrayList<>();

  public static final String USERS_OF_GROUP = "usersOfGroup";
  private List<UserOfGroupUpdate> usersOfGroup = new ArrayList<>();

  public OrgBulkUpdate() { 
  }

  public OrgBulkUpdate uri(URI uri) {
    
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


  public OrgBulkUpdate users(List<UserUpdate> users) {
    
    this.users = users;
    return this;
  }

  public OrgBulkUpdate addUsersItem(UserUpdate usersItem) {
    this.users.add(usersItem);
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

  public List<UserUpdate> getUsers() {
    return users;
  }


  @JsonProperty(USERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUsers(List<UserUpdate> users) {
    this.users = users;
  }


  public OrgBulkUpdate groups(List<GroupUpdate> groups) {
    
    this.groups = groups;
    return this;
  }

  public OrgBulkUpdate addGroupsItem(GroupUpdate groupsItem) {
    this.groups.add(groupsItem);
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

  public List<GroupUpdate> getGroups() {
    return groups;
  }


  @JsonProperty(GROUPS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setGroups(List<GroupUpdate> groups) {
    this.groups = groups;
  }


  public OrgBulkUpdate groupsOfGroup(List<GroupOfGroupUpdate> groupsOfGroup) {
    
    this.groupsOfGroup = groupsOfGroup;
    return this;
  }

  public OrgBulkUpdate addGroupsOfGroupItem(GroupOfGroupUpdate groupsOfGroupItem) {
    this.groupsOfGroup.add(groupsOfGroupItem);
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

  public List<GroupOfGroupUpdate> getGroupsOfGroup() {
    return groupsOfGroup;
  }


  @JsonProperty(GROUPS_OF_GROUP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setGroupsOfGroup(List<GroupOfGroupUpdate> groupsOfGroup) {
    this.groupsOfGroup = groupsOfGroup;
  }


  public OrgBulkUpdate usersOfGroup(List<UserOfGroupUpdate> usersOfGroup) {
    
    this.usersOfGroup = usersOfGroup;
    return this;
  }

  public OrgBulkUpdate addUsersOfGroupItem(UserOfGroupUpdate usersOfGroupItem) {
    this.usersOfGroup.add(usersOfGroupItem);
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

  public List<UserOfGroupUpdate> getUsersOfGroup() {
    return usersOfGroup;
  }


  @JsonProperty(USERS_OF_GROUP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUsersOfGroup(List<UserOfGroupUpdate> usersOfGroup) {
    this.usersOfGroup = usersOfGroup;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrgBulkUpdate orgBulkUpdate = (OrgBulkUpdate) o;
    return Objects.equals(this.uri, orgBulkUpdate.uri) &&
        Objects.equals(this.users, orgBulkUpdate.users) &&
        Objects.equals(this.groups, orgBulkUpdate.groups) &&
        Objects.equals(this.groupsOfGroup, orgBulkUpdate.groupsOfGroup) &&
        Objects.equals(this.usersOfGroup, orgBulkUpdate.usersOfGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, users, groups, groupsOfGroup, usersOfGroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrgBulkUpdate {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    groupsOfGroup: ").append(toIndentedString(groupsOfGroup)).append("\n");
    sb.append("    usersOfGroup: ").append(toIndentedString(usersOfGroup)).append("\n");
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
