package org.smartbit4all.api.invocation;

import java.net.URI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionManagementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    InvocationTestSessionConfig.class,
})
@TestInstance(Lifecycle.PER_CLASS)
@Disabled
class InvocationApiSession {

  private static final String PASSWD =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";

  private static final String USER1 = "USER1";

  private static final String USER2 = "USER2";

  private static final String USER3 = "USER3";

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private TestApi testApi;

  @Autowired
  private TestEventPublisherApi testEventPublisherApi;

  @Autowired
  OrgApi orgApi;

  // @Autowired
  // LocalAuthenticationService authService;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  private URI userUri1;

  private URI userUri2;

  private URI userUri3;

  @BeforeAll
  void setUpBeforeClass() throws Exception {
    // TODO This is hack to ensure the security options in the test after deleting the whole fs.
    userUri1 = orgApi.saveUser(new User().username(USER1)
        .password(PASSWD)
        .name("Creator Camile"));
    userUri2 = orgApi.saveUser(new User().username(USER2)
        .password(PASSWD)
        .name("Validator Valeriana"));
    userUri3 = orgApi.saveUser(new User().username(USER3)
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
  void testPrimary() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testPrimary(invocationApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @Test
  void testInvocationByTemplate() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testInvocationByTemplate(invocationApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @Test
  void testInvocationByBuilder() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testInvocationByBuilder(invocationApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @Test
  void testInvocationHandler() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testInvocationHandler(invocationApi, testApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @Test
  void testInvokeAsync() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testInvocationByBuilder(invocationApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @Test
  void testInvokeAt() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testInvokeAt(invocationApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @Test
  void testInvokeAsyncFlow() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testInvokeAsyncFlow(invocationApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @Test
  void testSubscription() throws Exception {
    run(USER1, PASSWD, () -> {
      try {
        InvocationApiTestStatic.testSubscription(invocationApi, testEventPublisherApi);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

}
