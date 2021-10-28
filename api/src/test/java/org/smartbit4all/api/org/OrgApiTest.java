package org.smartbit4all.api.org;

import java.net.URI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApiLocal;
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

}
