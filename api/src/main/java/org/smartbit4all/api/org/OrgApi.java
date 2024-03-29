/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.org;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.OrgBulkUpdate;
import org.smartbit4all.api.org.bean.OrgState;
import org.smartbit4all.api.org.bean.User;

public interface OrgApi {

  /**
   * Get all information of a user.
   */
  User getUser(URI userUri);

  /**
   * Get all information of the users.
   */
  List<User> getUsers(List<URI> userUris);

  /**
   * Return all users active, and inactive.
   */
  List<User> getAllUsers();

  /**
   * Returns all the active users.
   */
  List<User> getActiveUsers();

  /**
   * Returns all users set to inactive status.
   *
   */
  public List<User> getInactiveUsers();

  /**
   * Returns every group.
   */
  List<Group> getAllGroups();

  /**
   * Returns every user who is a member of the specified group or its subgroups.
   */
  List<User> getUsersOfGroup(URI groupUri);

  /**
   * Returns every user who is a member of the specified group or its parent groups.
   */
  List<User> getUsersOfGroupAndParentGroups(URI groupUri);

  /**
   * Return which groups the user is member of.
   */
  List<Group> getGroupsOfUser(URI userUri);

  /**
   * Get all information of a group.
   */
  Group getGroup(URI groupUri);

  InputStream getUserImage(URI userUri);

  InputStream getGroupImage(URI groupUri);

  /**
   * Get all information of user
   */
  User getUserByUsername(String username);

  /**
   * Get all information of user. During the search ignores username's difference between upper and
   * lower case letters.
   */
  User getUserByUsernameIgnoreCase(String username);

  /**
   * Adds User to storage
   *
   * @param user
   * @return
   */
  URI saveUser(User user);

  /**
   * Creates Group and adds it to storage.
   */
  URI saveGroup(Group group);

  /**
   * Binds user and group together by adding to usersOfGroup, and groupsOfUser storage.
   */
  void addUserToGroup(URI userUri, URI groupUri);


  /**
   * Delete user.
   */
  void removeUser(URI userUri);


  /**
   * Delete group.
   */
  void removeGroup(URI groupUri);

  /**
   * Remove user from group.
   */
  void removeUserFromGroup(URI userUri, URI groupUri);

  /**
   * Add group as a child group.
   */
  void addChildGroup(Group parentGroup, Group childGroup);

  /**
   * Return list of groups contained by the specified group and it's subgroups.
   */
  List<Group> getSubGroups(URI groupUri);

  /**
   * Return list of groups directly containd by the specified group. (No recursion.)
   */
  List<Group> getConnectingSubGroups(URI groupUri);

  /**
   * Get all information of a group.
   */
  Group getGroupByName(String name);


  /**
   * Remove child group from under the parent group in hierarchy. Child group does not have to be a
   * direct sub group.
   */
  void removeSubGroup(URI parentGroupUri, URI childGroupUri);

  /**
   * Update user.
   *
   * @param user
   * @return
   */
  URI updateUser(User user);

  /**
   * Update group.
   *
   * @param group
   * @return
   */
  URI updateGroup(Group group);

  /**
   * Restore previously deleted user.
   *
   * @param userUri
   */
  void restoreDeletedUser(URI userUri);

  default void updateUsername(User user, String username) {};

  /**
   * Update multiple records in one transaction.
   * 
   * @param update
   */
  default void bulkUpdate(OrgBulkUpdate update) {};

  /**
   * Returns the current state of the OrgApi.
   * 
   * @return
   */
  default OrgState getOrgState() {
    return new OrgState();
  };

}
