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
   * @return
   */
  List<Group> getGroups();

  /**
   * Returns every security user.
   * @return
   */
  List<User> getUsers();
  
  /**
   * Returns every user who is a member of the specified group.
   * @param groupUri
   * @return
   */
  List<User> getUsersOfGroup(URI groupUri);

  /**
   * Return which groups the user is member of.
   * @param userUri
   * @return
   */
  List<Group> getGroupsOfUser(URI userUri);

  /**
   * Get all information of a user.
   * 
   * @param userUri
   * @return
   */
  User getUser(URI userUri);
}
