package org.smartbit4all.api.gateway;

import java.net.URI;
import java.util.List;
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


  User createUser(User user);

  Group createGroup(String groupName, String name, String description, URI kind, URI parent,
      List<URI> children);
}
