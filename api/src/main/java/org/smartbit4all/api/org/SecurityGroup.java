package org.smartbit4all.api.org;

import java.util.List;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.session.UserSessionApi;

/**
 * The security group is an object representing a security group the user can be assigned to. The
 * security groups are defined in the code because the programmer can use it explicitly. The
 * instance of the {@link SecurityGroup} is identified by the fully qualified name of the variable
 * by default. We can set the name directly but it's not necessary. This object provides an api for
 * the developer to be able to check if the current user is assigned to the given group or not.
 * 
 * @author Peter Boros
 */
public final class SecurityGroup {

  /**
   * The name of the given security group.
   */
  private String name;

  /**
   * The documentation of the given security group.
   */
  private String description;

  /**
   * The api to access the current user and it's settings. If we doesn't have this api then the
   * check is always true. Without security everything is allowed.
   */
  private UserSessionApi userSessionApi;

  private OrgApi api;

  public SecurityGroup(String description, UserSessionApi userSessionApi,
      SecurityGroup... subGroups) {
    super();
    this.description = description;
    this.userSessionApi = userSessionApi;
  }

  public final String getName() {
    return name;
  }

  final void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Checks if the given group is assigned to the current user.
   * 
   * @return true if there is no api for accessing the user rights or if the group is not assigned
   *         to the user. Else we get false.
   */
  public boolean check() {
    if (api == null || name == null) {
      return true;
    }
    if (userSessionApi.currentUser() == null) {
      // If we are not logged in then we doesn't have any assigned group.
      return false;
    }
    // Naive implementation. Get all the groups we have for the user and try to find the group.
    List<Group> groupsOfUser = api.getGroupsOfUser(userSessionApi.currentUser().getUri());
    if (groupsOfUser == null) {
      return false;
    }

    return groupsOfUser.parallelStream().anyMatch(g -> name.equals(g.getName()));
  }

  final void setOrgApi(OrgApi api) {
    this.api = api;
  }

}
