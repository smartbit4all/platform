package org.smartbit4all.domain.meta;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EventPublisherHelperTest {

  private static EventPublisherHelper<MyEventPublisher> helper;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    helper = new EventPublisherHelper<>(MyEventPublisher.class, "api:/myApi");
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void test() {
    EventDefinitionString stringEvent = helper.publisher().stringEvent();
    Assertions.assertEquals(stringEvent.getUri().toString(), "api:/myApi/stringEvent");

    EventDefinitionString stringEvent2 = helper.publisher().stringEvent2();
    Assertions.assertEquals(stringEvent2.getUri().toString(), "api:/myApi/stringEvent2");

    helper.publisher().stringEvent().subscribe().add(s -> System.out.println(s))
        .add(s -> System.out.println(s.toUpperCase()));

    helper.publisher().myEvent().subscribe().whenStartWith("prefix.")
        .add(s -> System.out.println(s));

    helper.fire(helper.publisher().stringEvent(), "Alma");

    helper.fire(helper.publisher().myEvent(), "Alma");
    helper.fire(helper.publisher().myEvent(), "prefix.Korte");

  }

}
