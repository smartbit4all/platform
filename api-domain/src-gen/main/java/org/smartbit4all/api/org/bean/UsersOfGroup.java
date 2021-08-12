package org.smartbit4all.api.org.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.org.bean.User;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UsersOfGroup
 */

public class UsersOfGroup   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("groupUri")
  private URI groupUri;

  @JsonProperty("users")
  @Valid
  private List<User> users = new ArrayList<>();

  public UsersOfGroup uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public UsersOfGroup groupUri(URI groupUri) {
    this.groupUri = groupUri;
    return this;
  }

  /**
   * Get groupUri
   * @return groupUri
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public URI getGroupUri() {
    return groupUri;
  }

  public void setGroupUri(URI groupUri) {
    this.groupUri = groupUri;
  }

  public UsersOfGroup users(List<User> users) {
    this.users = users;
    return this;
  }

  public UsersOfGroup addUsersItem(User usersItem) {
    this.users.add(usersItem);
    return this;
  }

  /**
   * Get users
   * @return users
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UsersOfGroup usersOfGroup = (UsersOfGroup) o;
    return Objects.equals(this.uri, usersOfGroup.uri) &&
        Objects.equals(this.groupUri, usersOfGroup.groupUri) &&
        Objects.equals(this.users, usersOfGroup.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, groupUri, users);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UsersOfGroup {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    groupUri: ").append(toIndentedString(groupUri)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
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

