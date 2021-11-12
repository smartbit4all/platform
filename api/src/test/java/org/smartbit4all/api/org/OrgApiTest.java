package org.smartbit4all.api.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApiLocal;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    OrgApiTestConfig.class
})
class OrgApiTest {

  @Autowired
  private MyModuleSecurityOption security;

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private UserSessionApiLocal userSessionApi;

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void init() throws IOException {
    TestFileUtil.initTestDirectory();
  }


  @Test
  void testOrgApi() {
    // Prepare a real user to check the security.
    URI testUserUri = orgApi.saveUser(
        new User().name("Joe Public").username("joe").email("joe.public@smartbit4all.org"));

    // Without logging in we don't have any right!
    Assertions.assertFalse(security.admin.check());

    Group viewerGroup =
        orgApi.getGroupByName("org.smartbit4all.api.org.MyModuleSecurityOption.viewer");
    orgApi.addUserToGroup(testUserUri, viewerGroup.getUri());

    // Set the current user - we logged in with this user.
    userSessionApi.setCurrentUser(orgApi.getUser(testUserUri));

    // We already have this group --> we have the right to view
    Assertions.assertTrue(security.viewer.check());

    // We still don't have the admin group --> not allowed to do admin stuffs
    Assertions.assertFalse(security.admin.check());

    Assertions.assertEquals("org.smartbit4all.api.org.MyModuleSecurityOption.admin",
        security.admin.getName());
    Assertions.assertEquals("org.smartbit4all.api.org.MyModuleSecurityOption.editor",
        security.editor.getName());
    Assertions.assertEquals("org.smartbit4all.api.org.MyModuleSecurityOption.viewer",
        security.viewer.getName());

  }

  @Test
  void getGroupsTest() throws Exception {

    URI savedGroupUri = orgApi.saveGroup(new Group().name("getGroupTest"));
    Group savedGroup = orgApi.getGroup(savedGroupUri);
    assertNotNull(savedGroup);

    List<Group> items = orgApi.getAllGroups();
    int numOfGroups = items.size();
    assertNotEquals(0, numOfGroups);

    URI savedGroupUri2 = orgApi.saveGroup(new Group().name("getGroupTest2"));

    List<Group> items2 = orgApi.getAllGroups();
    assertEquals(numOfGroups + 1, items2.size());

  }

  @Test
  void getGroupByNameTest() {
    Group testGroup = new Group().name("getGroupByNameTest");
    URI savedGroup = orgApi.saveGroup(testGroup);
    assertNotNull(savedGroup);

    Group groupByName = orgApi.getGroupByName("getGroupByNameTest");
    assertEquals(testGroup, groupByName);
  }

  @Test
  void getUsersTest() {

    URI savedUserUri = orgApi.saveUser(new User().username("getUserTest"));
    User savedUser = orgApi.getUser(savedUserUri);
    assertNotNull(savedUser);

    List<User> items = orgApi.getActiveUsers();
    int numOfUser = items.size();
    assertNotEquals(0, numOfUser);

    URI savedUserUri2 = orgApi.saveUser(new User().username("getUserTest2"));

    List<User> items2 = orgApi.getActiveUsers();
    assertEquals(numOfUser + 1, items2.size());
  }

  @Test
  void getUserTest() throws Exception {

    assertThrows(ObjectNotFoundException.class,
        () -> orgApi.getUser(URI.create("non_existent_uri")));

    User testUser = new User().username("newUser");

    URI savedUser = orgApi.saveUser(testUser);
    User user = orgApi.getUser(savedUser);
    assertEquals(user, testUser);

  }

  @Test
  void getUserByNameTest() {
    User testUser = new User().username("getUserByNameTest");
    URI savedUser = orgApi.saveUser(testUser);
    assertNotNull(savedUser);

    User userByName = orgApi.getUserByUsername("getUserByNameTest");
    assertEquals(testUser, userByName);
  }

  @Test
  void userAndGroupBindingTest() {
    User testUser = new User().username("userBindingTest");
    URI user = orgApi.saveUser(testUser);
    Group testGroup = new Group().name("groupBindingTest");
    URI group = orgApi.saveGroup(testGroup);

    List<Group> emptyGroupsOfUser = orgApi.getGroupsOfUser(user);
    assertEquals(0, emptyGroupsOfUser.size());

    List<User> emptyUsersOfGroup = orgApi.getUsersOfGroup(group);
    assertEquals(0, emptyUsersOfGroup.size());

    orgApi.addUserToGroup(user, group);

    List<Group> groupsOfUser = orgApi.getGroupsOfUser(user);
    assertEquals(1, groupsOfUser.size());

    List<User> usersOfGroup = orgApi.getUsersOfGroup(group);
    assertEquals(1, usersOfGroup.size());
  }

  @Test
  void createUser() {
    URI createdUser = orgApi.saveUser(new User().username("createUser"));
    assertNotNull(createdUser);
    User user = orgApi.getUser(createdUser);
    assertNotNull(user);
  }

  @Test
  void createGroup() {
    URI createdGroup = orgApi.saveGroup(new Group().name("createGroup"));
    assertNotNull(createdGroup);
    Group group = orgApi.getGroup(createdGroup);
    assertNotNull(group);
  }

  @Test
  void removeUserTest() {
    URI savedUser = orgApi.saveUser(new User().username("removeUserTest"));

    User user = orgApi.getUser(savedUser);
    assertNotNull(user);

    orgApi.removeUser(savedUser);
    User removedUser = orgApi.getUser(savedUser);
    assertEquals(true, removedUser.getInactive());
  }

  @Test
  void restoreUserTest() {
    URI savedUser = orgApi.saveUser(new User().username("restoreUserTest"));

    User user = orgApi.getUser(savedUser);
    assertNotNull(user);

    orgApi.removeUser(savedUser);
    List<User> allUsers = orgApi.getActiveUsers();

    assertFalse(allUsers.contains(user));

    orgApi.restoreDeletedUser(savedUser);

    List<User> allUsers2 = orgApi.getActiveUsers();
    User reactivatedUser = orgApi.getUser(savedUser);
    assertFalse(reactivatedUser.getInactive());
    assertTrue(allUsers2.contains(reactivatedUser));

  }

  @Test
  void removeGroupTest() {
    URI savedGroup = orgApi.saveGroup(new Group().name("removeGroupTest"));
    Group group = orgApi.getGroup(savedGroup);
    assertNotNull(group);

    orgApi.removeGroup(savedGroup);
    List<Group> allGroups = orgApi.getAllGroups();

    assertFalse(allGroups.contains(group));
  }

  @Test
  void updateUserTest() {
    User oldUser = new User().username("updateUserTest");
    URI savedUser = orgApi.saveUser(oldUser);

    User newUser = new User().name("newName").email("newEmail").username("updateUserTest")
        .password("newPassword");
    URI updatedUser = orgApi.updateUser(newUser);

    User updatedUser2 = orgApi.getUser(oldUser.getUri());

    assertEquals(newUser, updatedUser2);
    assertNotNull(updatedUser);

  }

  @Test
  void removeUserFromGroupTest() {
    User testUser = createTestUser();
    URI testUserUri = orgApi.saveUser(testUser);
    Group testGroup = createTestGroup();
    URI testGroupUri = orgApi.saveGroup(testGroup);

    orgApi.addUserToGroup(testUserUri, testGroupUri);

    boolean inGroup = isUserInGroup(testUser, testGroup);
    assertTrue(inGroup);
    boolean hasUser = groupHasUser(testUser, testGroup);
    assertTrue(hasUser);

    orgApi.removeUserFromGroup(testUser.getUri(), testGroup.getUri());

    boolean inGroup2 = isUserInGroup(testUser, testGroup);
    assertFalse(inGroup2);
    boolean hasUser2 = groupHasUser(testUser, testGroup);
    assertFalse(hasUser2);
  }

  @Test
  void removeSubGgroupTest() {
    orgApi.saveGroup(new Group().name("Group1"));
    orgApi.saveGroup(new Group().name("Group2"));
    orgApi.saveGroup(new Group().name("Group3"));
    orgApi.saveGroup(new Group().name("Group4"));

    Group group1 = orgApi.getGroupByName("Group1");
    Group group2 = orgApi.getGroupByName("Group2");
    Group group3 = orgApi.getGroupByName("Group3");
    Group group4 = orgApi.getGroupByName("Group4");

    orgApi.addChildGroup(group1, group2);
    orgApi.addChildGroup(group2, group3);

    assertTrue(group1.getChildren().contains(group2.getUri()));
    assertTrue(group2.getChildren().contains(group3.getUri()));

    orgApi.removeSubGroup(group1.getUri(), group4.getUri());

    orgApi.removeSubGroup(group1.getUri(), group3.getUri());
    assertFalse(
        orgApi.getGroupByName("Group2").getChildren().contains(group3.getUri()));

    orgApi.removeSubGroup(group1.getUri(), group2.getUri());
    assertFalse(
        orgApi.getGroupByName("Group1").getChildren().contains(group2.getUri()));
  }

  private boolean groupHasUser(User testUser, Group testGroup) {
    List<User> usersOfGroup = orgApi.getUsersOfGroup(testGroup.getUri());
    boolean hasUser = usersOfGroup.stream().anyMatch(item -> item.equals(testUser));
    return hasUser;
  }

  private boolean isUserInGroup(User testUser, Group testGroup) {
    List<Group> groupsOfUser = orgApi.getGroupsOfUser(testUser.getUri());
    boolean hasGroup = groupsOfUser.stream().anyMatch(item -> item.equals(testGroup));
    return hasGroup;
  }

  private User createTestUser() {
    User user = new User().username("TEST_USERNAME");
    return user;
  }

  private Group createTestGroup() {
    Group group = new Group().name("TEST_GROUP");
    return group;
  }

}
