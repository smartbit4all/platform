package org.smartbit4all.testing.platformevent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.platformevent.PlatformEventApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.sec.config.SecurityLocalTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {PlatformApiConfig.class, TestFSConfig.class, SecurityLocalTestConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlatformEventApiFailTest {

  private static final Logger log = LoggerFactory.getLogger(PlatformEventApiFailTest.class);

  private static final String EVENT_CODE = "eventCode";

  @Autowired
  private PlatformEventApi platformEventApi;
  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Test
  @Order(1)
  public void publishEventFailTest() {

    assertThrows(NoCurrentSessionException.class, () -> {
      platformEventApi.createEvent(EVENT_CODE).publish();
    });

    sessionManagementApi.startTechnicalSession();

    try {

      assertFalse(platformEventApi.createEvent(EVENT_CODE).publish());

    } catch (Exception e) {
      log.debug("Not expected assertion thrown in [PlatformEventApiFailTest]");
      log.debug(e.getMessage());
      throw new AssertionError("Not expected assertion thrown in [PlatformEventApiFailTest]");
    }
  }

}
