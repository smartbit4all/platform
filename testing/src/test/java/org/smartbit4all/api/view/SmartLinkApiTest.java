package org.smartbit4all.api.view;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.view.bean.SmartLinkData;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.sec.localauth.LocalAuthenticationService;
import org.smartbit4all.testing.mdm.MDMApiTestConfig;
import org.smartbit4all.testing.mdm.MDMSecurityOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {MDMApiTestConfig.class}, properties = {
    "invocationregistry.refresh.fixeddelay=2000",
    "applicationruntime.maintain.fixeddelay=2000",
    "storage.useSecondInUri=true"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class SmartLinkApiTest {

  @Autowired
  private SmartLinkApi smartLinkApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  OrgApi orgApi;

  @Autowired
  ObjectApi objectApi;

  @Autowired
  SessionApi sessionApi;

  @Autowired
  private LocalAuthenticationService authService;

  private URI adminUri;

  private URI normalUri;

  private UUID viewContextUUID;

  private static final String PASSWD =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";
  private static final String admin = "user_admin";

  private static final String admin2 = "user_admin2";

  private static final String normal_user = "user_normal";

  @BeforeAll
  void setUpBeforeClass() throws Exception {
    sessionManagementApi.startSession();

    adminUri = createUser(admin, "Adminisztrátor Aladár", MDMSecurityOptions.admin);

    adminUri = createUser(admin2, "Adminisztrátor Árpád", MDMSecurityOptions.admin);

    normalUri = createUser(normal_user, "Publikus József");

  }

  private Map<UUID, String> smartLinkViewNames = new HashMap<>();

  @Test
  @Order(1)
  void testPublishingSmartLinks() throws Exception {

    authService.login(admin, "asd");
    // Create a given number of smartlink with the old storage approach.
    int count = 200;
    ((SmartLinkApiImpl) smartLinkApi).upgradeStorage = false;
    while (0 <= count--) {
      String viewName = "view" + count;
      URI uri = smartLinkApi.publishView("channel1", new View().viewName(viewName));
      UUID uuid = objectApi.loadLatest(uri).getValue(UUID.class, SmartLinkData.UUID);
      smartLinkViewNames.put(uuid, viewName);
    }
  }

  private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

  @Test
  @Order(2)
  void testAllSmartLinksOld() throws Exception {

    for (Entry<UUID, String> smartLinkEntry : smartLinkViewNames.entrySet()) {
      ObjectNode node = smartLinkApi.getSmartLink("channel1", smartLinkEntry.getKey());
      Assertions.assertThat(node.getValueAsString(SmartLinkData.VIEW, View.VIEW_NAME))
          .isEqualTo(smartLinkEntry.getValue());
    }

  }

  @Test
  @Order(3)
  void testMigratingSmartLinks() throws Exception {

    ((SmartLinkApiImpl) smartLinkApi).upgradeStorage = true;
    assertThrows(IllegalStateException.class,
        () -> smartLinkApi.getSmartLink("channel1", UUID.randomUUID()));
    executor.schedule(() -> assertThrows(IllegalStateException.class,
        () -> smartLinkApi.getSmartLink("channel1", UUID.randomUUID())), 1, TimeUnit.SECONDS);
    smartLinkApi.migrate("channel1");

  }

  @Test
  @Order(4)
  void testAllSmartLinks() throws Exception {

    for (Entry<UUID, String> smartLinkEntry : smartLinkViewNames.entrySet()) {
      ObjectNode node = smartLinkApi.getSmartLink("channel1", smartLinkEntry.getKey());
      Assertions.assertThat(node.getValueAsString(SmartLinkData.VIEW, View.VIEW_NAME))
          .isEqualTo(smartLinkEntry.getValue());
    }

  }

  @Test
  @Order(5)
  void testCreateSmartLinksWithUpgradedStorage() throws Exception {

    int count = 10;
    Map<UUID, String> smartLinkViewNames2 = new HashMap<>();
    while (0 <= count--) {
      String viewName = "view" + count;
      URI uri = smartLinkApi.publishView("channel1", new View().viewName(viewName));
      UUID uuid = objectApi.loadLatest(uri).getValue(UUID.class, SmartLinkData.UUID);
      smartLinkViewNames2.put(uuid, viewName);
    }

    for (Entry<UUID, String> smartLinkEntry : smartLinkViewNames2.entrySet()) {
      ObjectNode node = smartLinkApi.getSmartLink("channel1", smartLinkEntry.getKey());
      Assertions.assertThat(node.getValueAsString(SmartLinkData.VIEW, View.VIEW_NAME))
          .isEqualTo(smartLinkEntry.getValue());
    }

  }

  private URI createUser(String username, String fullname, SecurityGroup... group) {
    URI uri = orgApi.saveUser(new User().username(username).password(PASSWD).name(fullname));
    Arrays.asList(group).stream()
        .forEach(g -> orgApi.addUserToGroup(uri, orgApi.getGroupByName(g.getName()).getUri()));

    return uri;
  }

}
