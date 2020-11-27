package org.smartbit4all.api.org;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;

public interface OrgApi {

  User getUser(URI userUri);
  
  List<User> getAllUsers();
  
  List<User> getUsersOfGroup(URI groupUri);
  
  List<Group> getGroupsOfUser(URI userUri);
  
  Group getGroup(URI groupUri);
  
  List<Group> getRootGroups();
}
