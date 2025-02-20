package org.smartbit4all.api.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.org.bean.UserLastAccess;
import org.smartbit4all.api.org.bean.UserSecurityPolicy;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.domain.application.TimeManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = {
    OrgApiTestConfig.class
})

class UserSecurityCheckerApiTest {

  @Autowired
  private UserSecurityCheckerApi userSecurityCheckerApi;

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private MasterDataManagementApi mdmApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private TimeManagementService timeManagementService;

  URI testUserUriWithLogin;

  URI testUserUriWithouthLogin;

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    TestFileUtil.clearTestDirectory();
  }


  @BeforeAll
  void init() throws IOException {
    TestFileUtil.clearTestDirectory();
    TestFileUtil.initTestDirectory();
    orgApi.getActiveUsers().stream().forEach(user -> orgApi.removeUser(user.getUri()));;
  }

  @Test
  void testSecurityCheck() {
    MDMEntryApi mdmEntryApi =
        mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            UserSecurityCheckerApi.MDM_NAME);

    mdmEntryApi.save(objectApi.create("test", new UserSecurityPolicy().name("test")
        .inactivityLockoutDays(1l).passwordExpirationDays(1l).passwordReminderDays(1l)));

    testUserUriWithLogin = orgApi.saveUser(
        new User().name("Joe Active").username("joeA").email("joe.Active@smartbit4all.org"));

    testUserUriWithouthLogin = orgApi.saveUser(
        new User().name("Joe Inactive").username("joeI").email("joe.Inactive@smartbit4all.org"));

    userSecurityCheckerApi.updateOrCreateUserLastAccess(testUserUriWithLogin,
        UserLastAccess.REGISTRATION_DATE);
    userSecurityCheckerApi.updateOrCreateUserLastAccess(testUserUriWithLogin,
        UserLastAccess.LAST_PASSWORD_CHANGE);
    userSecurityCheckerApi.updateOrCreateUserLastAccess(testUserUriWithLogin,
        UserLastAccess.LAST_LOGIN);

    userSecurityCheckerApi.updateOrCreateUserLastAccess(testUserUriWithouthLogin,
        UserLastAccess.REGISTRATION_DATE);
    userSecurityCheckerApi.checkUsersBySecurityPolicy();

    assertEquals(2, orgApi.getActiveUsers().size());
    OffsetDateTime now = OffsetDateTime.now(timeManagementService.getSynchronizedClock());
    updateUserWithoutLogin(now);
    userSecurityCheckerApi.checkUsersBySecurityPolicy();

    assertEquals(1, orgApi.getActiveUsers().size());

    updateUserWithLogin(now);

    userSecurityCheckerApi.checkUsersBySecurityPolicy();
    assertEquals(1, orgApi.getActiveUsers().size());

    updateUserWithExpiredPassword(now);

    userSecurityCheckerApi.checkUsersBySecurityPolicy();
    assertEquals(0, orgApi.getActiveUsers().size());
  }


  private void updateUserWithExpiredPassword(OffsetDateTime now) {
    ObjectNode userNodeWithLogin = objectApi.loadLatest(testUserUriWithLogin);

    ObjectNodeReference lastAccesRefWithLogin =
        userNodeWithLogin.ref(UserSecurityCheckerApi.LAST_ACCESS_IN_ORG);

    ObjectNode lastAccesNodeWithLogin =
        lastAccesRefWithLogin.get();

    lastAccesNodeWithLogin.setValue(
        now.minusDays(2l),
        UserLastAccess.LAST_PASSWORD_CHANGE);
    objectApi.save(userNodeWithLogin);
  }


  private void updateUserWithLogin(OffsetDateTime now) {
    ObjectNode userNodeWithLogin = objectApi.loadLatest(testUserUriWithLogin);

    ObjectNodeReference lastAccesRefWithLogin =
        userNodeWithLogin.ref(UserSecurityCheckerApi.LAST_ACCESS_IN_ORG);

    ObjectNode lastAccesNodeWithLogin =
        lastAccesRefWithLogin.get();

    lastAccesNodeWithLogin.setValue(
        now.minusDays(2l),
        UserLastAccess.REGISTRATION_DATE).setValue(now, UserLastAccess.LAST_LOGIN);
    lastAccesRefWithLogin.set(lastAccesNodeWithLogin);
    objectApi.save(userNodeWithLogin);
  }


  private void updateUserWithoutLogin(OffsetDateTime now) {
    ObjectNode userNodeWithoutLogin = objectApi.loadLatest(testUserUriWithouthLogin);

    ObjectNodeReference lastAccesRefWithoutLogin =
        userNodeWithoutLogin.ref(UserSecurityCheckerApi.LAST_ACCESS_IN_ORG);

    ObjectNode lastAccesNodeWithoutLogin =
        lastAccesRefWithoutLogin.get();

    lastAccesNodeWithoutLogin.setValue(
        now.minusDays(2l),
        UserLastAccess.REGISTRATION_DATE);
    lastAccesRefWithoutLogin.set(lastAccesNodeWithoutLogin);
    objectApi.save(userNodeWithoutLogin);
  }

}
