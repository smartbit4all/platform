package org.smartbit4all.api.org.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.org.bean.Group;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * GroupsOfUser
 */

public class GroupsOfUser   {
  @JsonProperty("userUri")
  private URI userUri;

  @JsonProperty("groups")
  @Valid
  private List<Group> groups = new ArrayList<>();

  public GroupsOfUser userUri(URI userUri) {
    this.userUri = userUri;
    return this;
  }

  /**
   * Get userUri
   * @return userUri
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public URI getUserUri() {
    return userUri;
  }

  public void setUserUri(URI userUri) {
    this.userUri = userUri;
  }

  public GroupsOfUser groups(List<Group> groups) {
    this.groups = groups;
    return this;
  }

  public GroupsOfUser addGroupsItem(Group groupsItem) {
    this.groups.add(groupsItem);
    return this;
  }

  /**
   * Get groups
   * @return groups
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<Group> getGroups() {
    return groups;
  }

  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupsOfUser groupsOfUser = (GroupsOfUser) o;
    return Objects.equals(this.userUri, groupsOfUser.userUri) &&
        Objects.equals(this.groups, groupsOfUser.groups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userUri, groups);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupsOfUser {\n");
    
    sb.append("    userUri: ").append(toIndentedString(userUri)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
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

