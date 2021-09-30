package org.smartbit4all.api.gateway;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.security.bean.AthenticationResult;

public interface SecurityGatewayService {

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
   * @throws Exception
   */
  List<Group> getGroups();

  /**
   * Returns every security user.
   * 
   * @return
   * @throws Exception
   */
  List<User> getUsers();

  /**
   * Returns every user who is a member of the specified group.
   * 
   * @param groupUri
   * @return
   * @throws Exception
   */
  List<User> getUsersOfGroup(URI groupUri);

  /**
   * Return which groups the user is member of.
   * 
   * @param userUri
   * @return
   * @throws Exception
   */
  List<Group> getGroupsOfUser(URI userUri);

  /**
   * Get all information of a user.
   * 
   * @param userUri
   * @return
   * @throws Exception
   */
  User getUser(URI userUri);

  /**
   * Get all information of user
   * 
   * @param username
   * @return user with the given username
   */
  Optional<User> getUserByUsername(String username);


  /**
   * Adds User to storage
   * 
   * @param user
   * @return
   */
  User saveUser(User user);

  /**
   * Creates Group and adds it to storage.
   * 
   * @param groupName
   * @param name
   * @param description
   * @param kind
   * @param parent
   * @param children
   * @return
   */
  Group saveGroup(String name, String description, URI kind, URI parent,
      List<URI> children);

  /**
   * Creates Group and adds it to storage.
   * 
   * @param group
   * @return
   */
  Group saveGroup(Group group);

  /**
   * Binds user and group together by adding to usersOfGroup, and groupsOfUser storage.
   * 
   * @param user
   * @param group
   */
  void addUserToGroup(User user, Group group);


  /**
   * Delete user.
   * 
   * @param userUri
   */
  void removeUser(URI userUri);


  /**
   * Delete group.
   * 
   * @param groupUri
   */
  void removeGroup(URI groupUri);


  /**
   * Update user.
   * 
   * @param user
   * @return
   */
  User updateUser(User user);

  /**
   * Update group.
   * 
   * @param group
   * @return
   */
  Group updateGroup(Group group);

  /**
   * Remove user from group.
   * 
   * @param userUri
   * @param groupUri
   */
  void removeUserFromGroup(URI userUri, URI groupUri);

  /**
   * Add group as a child group.
   * 
   * @param parentGroup
   * @param childGroup
   */
  void addChildGroup(Group parentGroup, Group childGroup);

  /**
   * Get all information of a group.
   * 
   * @param groupUri
   * @return
   */
  Group getGroup(URI groupUri);

  /**
   * Return list of groups contained by group.
   * 
   * @param groupUri
   * @return
   */
  List<Group> getSubGroups(URI groupUri);

  /**
   * Get all information of a group.
   * 
   * @param name
   * @return group with the given name
   */
  Optional<Group> getGroupByName(String name);
}
