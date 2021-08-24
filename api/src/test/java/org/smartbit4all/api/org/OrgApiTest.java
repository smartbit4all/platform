package org.smartbit4all.api.org;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApiLocal;

class OrgApiTest {

  private static MyModuleSecurityOption security = new MyModuleSecurityOption();

  private static OrgApiInMemory orgApi = new OrgApiInMemory(null);
  
  private static UserSessionApiLocal userSessionApi = new UserSessionApiLocal();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    orgApi.analyzeSecurityOptions(security);
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testOrgApi() {
    // Prepare a real user to check the security.
    User testUser = orgApi.addTestUser("Joe Public", "joe", "joe.public@smartbit4all.org");
    
    // Without logging in we don't have any right!
    Assertions.assertFalse(security.admin.check());


    orgApi.addTestGroup("org.smartbit4all.api.org.MyModuleSecurityOption.viewer", testUser);

    // Set the current user - we logged in with this user.
    userSessionApi.setCurrentUser(testUser);

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
