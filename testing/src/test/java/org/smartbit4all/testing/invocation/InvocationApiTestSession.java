package org.smartbit4all.testing.invocation;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationTestConfig;
import org.smartbit4all.api.invocation.TestApi;
import org.smartbit4all.api.invocation.TestEventPublisherApi;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.testing.mdm.MDMApiTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    MDMApiTestConfig.class, InvocationTestConfig.class,
}, properties = {
    "invocationregistry.refresh.fixeddelay=5000",
    "session.timeout-min=1",
    "applicationruntime.maintain.fixeddelay=2000",
    "applicationsetup.schedule.initdelay=1000",
    "applicationsetup.schedule.fixeddelay=200",
})
@TestInstance(Lifecycle.PER_CLASS)
@Disabled
public class InvocationApiTestSession {

  private static final String PASSWD =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private TestApi testApi;

  @Autowired
  private TestEventPublisherApi testEventPublisherApi;

  @Autowired
  OrgApi orgApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private SessionApi sessionApi;

  private URI userUri1;

  private URI userUri2;

  private URI userUri3;

  @BeforeAll
  void setUpBeforeClass() throws Exception {
    userUri1 = orgApi.saveUser(new User().username(InvocationTestConfig.USER1)
        .password(PASSWD)
        .name("Creator Camile"));
    userUri2 = orgApi.saveUser(new User().username(InvocationTestConfig.USER2)
        .password(PASSWD)
        .name("Validator Valeriana"));
    userUri3 = orgApi.saveUser(new User().username(InvocationTestConfig.USER3)
        .password(PASSWD)
        .name("Editor Edvin"));
  }

  private void run(String userName, String passwd, Runnable func) throws Exception {
    // SessionInfoData sessionInfoData = sessionManagementApi.startSession();
    // authService.login(USER1, "asd");
    func.run();
    // authService.logout();
  }

  @Test
  @DisplayName("Create technical session only once, but renew when expired")
  void testTechnicalSessionRenw() throws Exception {
    Set<URI> sessionUriSet = new HashSet<>();
    sessionManagementApi.startTechnicalSessionWithUser(InvocationTestConfig.USER1);
    sessionUriSet.add(sessionApi.getSessionUri());
    // Wait a short time
    Thread.sleep(100);
    sessionManagementApi.startTechnicalSessionWithUser(InvocationTestConfig.USER1);
    sessionUriSet.add(sessionApi.getSessionUri());
    Thread.sleep(61 * 1000);
    sessionManagementApi.startTechnicalSessionWithUser(InvocationTestConfig.USER1);
    sessionUriSet.add(sessionApi.getSessionUri());

    org.assertj.core.api.Assertions.assertThat(sessionUriSet).hasSize(2);
  }

}
