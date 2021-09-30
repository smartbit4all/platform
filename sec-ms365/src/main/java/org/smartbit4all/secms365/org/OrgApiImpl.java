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
package org.smartbit4all.secms365.org;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.sec.service.SecurityService;
import org.smartbit4all.secms365.service.MsGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import com.microsoft.graph.requests.extensions.IGroupCollectionPage;
import com.microsoft.graph.requests.extensions.IGroupCollectionRequestBuilder;
import com.microsoft.graph.requests.extensions.IGroupCollectionWithReferencesPage;
import com.microsoft.graph.requests.extensions.IUserCollectionPage;
import com.microsoft.graph.requests.extensions.IUserCollectionRequestBuilder;
import com.microsoft.graph.requests.extensions.IUserCollectionWithReferencesPage;

@Service
@SessionScope
public class OrgApiImpl implements OrgApi {

  private static final String USER_SELECT = "Id,DisplayName,Mail";

  private MsGraphService graphService;

  @Autowired
  private SecurityService securityService;

  public OrgApiImpl(MsGraphService graphService) {
    this.graphService = graphService;
  }

  @Override
  public User getUser(URI userUri) {
    com.microsoft.graph.models.extensions.User user = graphService.getGraphClient()
        .users(userUri.getFragment())
        .buildRequest()
        .select(USER_SELECT)
        .get();

    return createUser(user);
  }

  @Override
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    IUserCollectionPage userPage =
        graphService.getGraphClient().users().buildRequest().top(25).get();
    while (userPage != null) {
      List<com.microsoft.graph.models.extensions.User> currentPage = userPage.getCurrentPage();
      users.addAll(currentPage.stream().map(u -> createUser(u)).collect(Collectors.toList()));
      IUserCollectionRequestBuilder nextPage = userPage.getNextPage();
      if (nextPage == null) {
        break;
      } else {
        userPage = nextPage.buildRequest().get();
      }
    }
    return users;
  }

  @Override
  public List<Group> getAllGroups() {
    List<Group> groups = new ArrayList<>();
    IGroupCollectionPage groupPage =
        graphService.getGraphClient().groups().buildRequest().top(25).get();
    while (groupPage != null) {
      List<com.microsoft.graph.models.extensions.Group> currentPage = groupPage.getCurrentPage();
      groups.addAll(currentPage.stream().map(u -> createGroup(u)).collect(Collectors.toList()));
      IGroupCollectionRequestBuilder nextPage = groupPage.getNextPage();
      if (nextPage == null) {
        break;
      } else {
        groupPage = nextPage.buildRequest().get();
      }
    }
    return groups;
  }

  @Override
  public List<User> getUsersOfGroup(URI groupUri) {
    IUserCollectionWithReferencesPage usersPage = graphService.getGraphClient()
        .groups(groupUri.getFragment())
        .membersAsUser()
        .buildRequest().get();
    List<User> users = usersPage.getCurrentPage()
        .stream()
        .map(u -> createUser(u))
        .collect(Collectors.toList());
    return users;
  }

  @Override
  public List<Group> getGroupsOfUser(URI userUri) {
    IGroupCollectionWithReferencesPage groupsWithRefPage = graphService.getGraphClient()
        .users(userUri.getFragment())
        .transitiveMemberOfAsGroup()
        .buildRequest().get();
    List<com.microsoft.graph.models.extensions.Group> groups = groupsWithRefPage.getCurrentPage();
    List<Group> retGroups = groups.stream()
        .map(g -> createGroup(g))
        .collect(Collectors.toList());
    return retGroups;
  }

  @Override
  public Group getGroup(URI groupUri) {
    com.microsoft.graph.models.extensions.Group group = graphService.getGraphClient()
        .groups(groupUri.getFragment())
        .buildRequest()
        .select("Id,DisplayName,Description")
        .get();
    return createGroup(group);
  }

  @Override
  public List<Group> getRootGroups() {
    IGroupCollectionPage groupPage = graphService.getGraphClient().groups().buildRequest().get();
    List<Group> groups = groupPage.getCurrentPage().stream()
        .map(g -> createGroup(g))
        .collect(Collectors.toList());
    return groups;
  }

  @Override
  public InputStream getUserImage(URI userUri) {
    InputStream userImageIs = graphService.getGraphClient()
        .users(userUri.getFragment())
        .photo()
        .content()
        .buildRequest().get();
    return userImageIs;
  }

  @Override
  public InputStream getGroupImage(URI groupUri) {
    InputStream userImageIs = graphService.getGraphClient()
        .groups(groupUri.getFragment())
        .photo()
        .content()
        .buildRequest().get();
    return userImageIs;
  }

  private User createUser(com.microsoft.graph.models.extensions.User user) {
    return new User()
        .uri(URI.create("user:/id#" + user.id))
        .username(user.displayName.replaceAll("\\s", ""))
        .name(user.displayName)
        .email(user.mail);
  }

  private User createUser(DefaultOidcUser user) {
    return new User()
        .uri(URI.create("user:/id#" + user.getAttribute("oid")))
        .username(user.getName().replaceAll("\\s", ""))
        .name(user.getName())
        .email(user.getEmail());
  }

  private Group createGroup(com.microsoft.graph.models.extensions.Group group) {
    URI uri = URI.create("group:/id#" + group.id);
    return new Group()
        .uri(uri)
        .name(group.displayName)
        .description(group.description);
  }

  @Override
  public User getUserByUsername(String username) {
    IUserCollectionPage userPage =
        graphService.getGraphClient().users().buildRequest().top(25).get();
    while (userPage != null) {
      List<com.microsoft.graph.models.extensions.User> currentPage = userPage.getCurrentPage();
      for (Iterator<com.microsoft.graph.models.extensions.User> i = currentPage.iterator(); i
          .hasNext();) {
        com.microsoft.graph.models.extensions.User next = i.next();
        String displayName = next.displayName.replaceAll("\\s", "");
        if (displayName.equals(username)) {
          return createUser(next);
        }
      }
      IUserCollectionRequestBuilder nextPage = userPage.getNextPage();
      if (nextPage == null) {
        break;
      }
      userPage = nextPage.buildRequest().get();
    }
    return null;
  }
}
