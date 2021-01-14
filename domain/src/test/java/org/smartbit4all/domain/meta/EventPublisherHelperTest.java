package org.smartbit4all.domain.meta;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EventPublisherHelperTest {

  private static EventPublisherHelper<EventPublisherTest> helper;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    helper = new EventPublisherHelper<>(EventPublisherTest.class, "api:/myApi");
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void test() {
    EventDefinitionString stringEvent = helper.publisher().stringEvent();
    Assertions.assertEquals(stringEvent.getUri().toString(), "api:/myApi/stringEvent");

    helper.publisher().stringEvent().subscribe().add(new EventListener<String>() {

      @Override
      public void accept(String t) {
        System.out.println(t);
      }

    });

    helper.fire(helper.publisher().stringEvent(), "Alma");

  }

}
