package org.smartbit4all.sec.org;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import org.smartbit4all.api.gateway.SecurityGatewayService;
import org.smartbit4all.api.org.OrgApiImpl;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.springframework.core.env.Environment;

public class OrgApiGatewayImpl extends OrgApiImpl {

  private SecurityGatewayService secGatewayService;

  public OrgApiGatewayImpl(SecurityGatewayService secGatewayService, Environment env) {
    super(env);
    this.secGatewayService = secGatewayService;
  }

  @Override
  public List<Group> getAllGroups() {
    return secGatewayService.getGroups();
  }

  @Override
  public User getUser(URI userUri) {
    User user = secGatewayService.getUser(userUri);
    return user;
  }

  @Override
  public List<User> getAllUsers() {
    return secGatewayService.getUsers();
  }

  @Override
  public List<User> getUsersOfGroup(URI groupUri) {
    return secGatewayService.getUsersOfGroup(groupUri);
  }


  @Override
  public List<Group> getGroupsOfUser(URI userUri) {
    List<Group> result = secGatewayService.getGroupsOfUser(userUri);
    result.addAll(getAdditionalLocalGroups(result));
    return result;
  }

  @Override
  public Group getGroup(URI groupUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Group> getRootGroups() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getUserImage(URI userUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getGroupImage(URI groupUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getUserByUsername(String username) {
    List<User> users = secGatewayService.getUsers();
    for (User user : users) {
      if (username.equals(user.getUsername())) {
        return user;
      }
    }
    return null;
  }

}
