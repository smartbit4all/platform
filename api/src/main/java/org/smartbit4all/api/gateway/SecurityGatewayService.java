package org.smartbit4all.api.gateway;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.security.bean.AthenticationResult;

public interface SecurityGatewayService {

  public static final String SCHEME = "security";

  /**
   * Authenticate with username and password.
   * 
   * @param username
   * @param password
   * @return
   */
  AthenticationResult authenticate(String username, String password);

  /**
   * Returns every security group.
   * 
   * @return
   */
  @Deprecated
  List<Group> getGroups();

  /**
   * Returns every security user.
   * 
   * @return
   */
  @Deprecated
  List<User> getUsers();

  /**
   * Returns every user who is a member of the specified group.
   * 
   * @param groupUri
   * @return
   */
  @Deprecated
  List<User> getUsersOfGroup(URI groupUri);

  /**
   * Return which groups the user is member of.
   * 
   * @param userUri
   * @return
   */
  @Deprecated
  List<Group> getGroupsOfUser(URI userUri);

  /**
   * Get all information of a user.
   * 
   * @param userUri
   * @return
   */
  @Deprecated
  User getUser(URI userUri);

  /**
   * Get all information of user
   * 
   * @param username
   * @return user with the given username
   */
  @Deprecated
  User getUserByUsername(String username);


  /**
   * Adds User to storage
   * 
   * @param user
   * @return
   */
  @Deprecated
  URI saveUser(User user);

  /**
   * Creates Group and adds it to storage.
   * 
   * @param name
   * @param description
   * @param kind
   * @param parent
   * @param children
   * @return
   */
  @Deprecated
  URI saveGroup(String name, String description, URI kind, URI parent,
      List<URI> children);

  /**
   * Creates Group and adds it to storage.
   * 
   * @param group
   * @return
   */
  @Deprecated
  URI saveGroup(Group group);

  /**
   * Binds user and group together by adding to usersOfGroup, and groupsOfUser storage.
   * 
   * @param userUri
   * @param groupUri
   */
  @Deprecated
  void addUserToGroup(URI userUri, URI groupUri);


  /**
   * Delete user.
   * 
   * @param userUri
   */
  @Deprecated
  void removeUser(URI userUri);


  /**
   * Delete group.
   * 
   * @param groupUri
   */
  @Deprecated
  void removeGroup(URI groupUri);


  /**
   * Update user.
   * 
   * @param user
   * @return
   */
  @Deprecated
  URI updateUser(User user);

  /**
   * Update group.
   * 
   * @param group
   * @return
   */
  @Deprecated
  URI updateGroup(Group group);

  /**
   * Remove user from group.
   * 
   * @param userUri
   * @param groupUri
   */
  @Deprecated
  void removeUserFromGroup(URI userUri, URI groupUri);

  /**
   * Add group as a child group.
   * 
   * @param parentGroup
   * @param childGroup
   */
  @Deprecated
  void addChildGroup(Group parentGroup, Group childGroup);

  /**
   * Get all information of a group.
   * 
   * @param groupUri
   * @return
   */
  @Deprecated
  Group getGroup(URI groupUri);

  /**
   * Return list of groups contained by group.
   * 
   * @param groupUri
   * @return
   */
  @Deprecated
  List<Group> getSubGroups(URI groupUri);

  /**
   * Get all information of a group.
   * 
   * @param name
   * @return group with the given name
   */
  @Deprecated
  Group getGroupByName(String name);


  /**
   * Remove child group from under the parent group in hierarchy. Child group does not have to be a
   * direct sub group.
   * 
   * @param parentGroupUri
   * @param childGroupUri
   */
  @Deprecated
  void removeSubGroup(URI parentGroupUri, URI childGroupUri);
}
