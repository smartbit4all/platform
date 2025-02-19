package org.smartbit4all.testing.platformevent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.platformevent.PlatformEventApi;
import org.smartbit4all.api.platformevent.bean.PlatformEvent;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {PlatformEventApiTestConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlatformEventApiSuccesTest {

  private static final Logger log = LoggerFactory.getLogger(PlatformEventApiSuccesTest.class);

  private static final String EVENT_CODE = "eventCode";
  private static final String EVENT_CATEGORY = "eventCategory";
  private static final String PARAMETER_KEY = "parameterKey";
  private static final String PARAMETER_VALUE = "parameterValue";
  private static final String RELATED_KEY = "relatedKey";
  private static final URI RELATED_VALUE = URI.create("relatedValue");
  private static final String MESSAGE = "unique-message";

  @Autowired
  private PlatformEventApi platformEventApi;
  @Autowired
  private SessionManagementApi sessionManagementApi;
  @Autowired
  private SessionApi sessionApi;
  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private ObjectApi objectApi;

  @Test
  @Order(1)
  public void publishEventFailTest() {
    sessionManagementApi.startTechnicalSession();

    try {
      assertTrue(
          platformEventApi.createEvent(EVENT_CODE)
              .category(EVENT_CATEGORY)
              .addParameter(PARAMETER_KEY, PARAMETER_VALUE)
              .message(MESSAGE)
              .addRelatedItem(RELATED_KEY, RELATED_VALUE)
              .publish());

      PlatformEvent event =
          objectApi.loadLatest(getStoredList().uris().getLast()).getObject(PlatformEvent.class);
      assertThat(event)
          .satisfies(e -> {
            assertNotNull(e.getEventCode());
            assertNotNull(e.getEventCategory());
            assertNotNull(e.getParameters());
            assertNotNull(e.getUri());
            assertNotNull(e.getSessionUri());
            assertNotNull(e.getTimestamp());

            assertEquals(sessionApi.getUserUri(), e.getUserUri());
            assertEquals(sessionApi.getSessionUri(), e.getSessionUri());
            assertEquals(e.getEventMessage(), MESSAGE);
            assertEquals(e.getParameters().get(PARAMETER_KEY), PARAMETER_VALUE);
            assertEquals(e.getRelatedObjects().get(RELATED_KEY), RELATED_VALUE);
          });

    } catch (Exception e) {
      log.debug(e.getMessage());
      throw new AssertionError(e.getMessage());
    }



    // PlatformEventContributionApi.

  }

  private StoredList getStoredList() {
    return collectionApi.list(PlatformEventApiTestContributor.TEST_SCHEMA,
        PlatformEventApiTestContributor.EVENT_LIST);
  }

}
