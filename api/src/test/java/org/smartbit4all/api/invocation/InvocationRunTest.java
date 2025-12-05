package org.smartbit4all.api.invocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRun;
import org.smartbit4all.core.object.ContextObject;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = {
    InvocationTestStorageConfig.class,
}, properties = {
    "invocationregistry.refresh.fixeddelay=5000",
    "applicationruntime.maintain.fixeddelay=2000",
    "applicationsetup.schedule.initdelay=1000",
    "applicationsetup.schedule.fixeddelay=200",
})
@TestMethodOrder(OrderAnnotation.class)
public class InvocationRunTest {


  private static final String TEST_MAP = "testMap";
  private static final String COUNTER_LESS_THAN3 = "counterLessThan3";
  private static final String COUNTER = "counter";
  private static final String TEST_VALUE_HELLO = "hello";
  private static final List<String> RESULT_PATH = List.of(TEST_MAP, "result");
  private static final List<String> RESULT_2_PATH = List.of(TEST_MAP, "result2");
  private static final List<String> COUNTER_PATH = List.of(TEST_MAP, COUNTER);
  private static final List<String> COUNTER_LESS_THAN_3_PATH =
      List.of(TEST_MAP, COUNTER_LESS_THAN3);
  @Autowired
  private InvocationApiImpl invocationApi;
  @Autowired
  private ObjectApi objectApi;
  private ContextObject ctx;

  @BeforeEach
  void setUp() {
    ctx = objectApi.contextObject();
    ctx.set(TEST_MAP, new HashMap<>());
  }

  @Test
  @Order(10)
  void successWithApplyResult() {
    InvocationRequest request = testInvocationRequest("invocationReturnInput")
        .addParametersItem(new InvocationParameter()
            .name("input").typeClass(Object.class.getName()).value(TEST_VALUE_HELLO));

    InvocationRun run = new InvocationRunBuilder()
        .addItem(InvocationRunItemBuilder.withRequest(request)
            .applyResult(RESULT_PATH, List.of(ContextObject.INVOCATION_RESULT)))
        .build();

    invocationApi.run(ctx, run);

    assertEquals(TEST_VALUE_HELLO, ctx.getValueFromContext(RESULT_PATH));
  }

  @Test
  @Order(11)
  void successThenMappingResolveTest() {
    InvocationRequest request = testInvocationRequest("invocationReturnInput")
        .addParametersItem(new InvocationParameter()
            .name("input").typeClass(Object.class.getName()).value(TEST_VALUE_HELLO));

    InvocationRun run = new InvocationRunBuilder()
        .addItem(InvocationRunItemBuilder.withRequest(request)
            .applyResult(RESULT_PATH, List.of(ContextObject.INVOCATION_RESULT)))
        .build();

    invocationApi.run(ctx, run);
    InvocationRequest request2 = testInvocationRequest("invocationReturnInput")
        .addParametersItem(new InvocationParameter()
            .name("input").typeClass(Object.class.getName()));

    InvocationRun run2 = new InvocationRunBuilder()
        .addItem(InvocationRunItemBuilder.withRequest(request2)
            .addResolver(mb -> mb.addMapping(map -> map.fromPath(RESULT_PATH)))
            .applyResult(RESULT_2_PATH, List.of(ContextObject.INVOCATION_RESULT)))
        .build();

    invocationApi.run(ctx, run2);

    assertEquals(TEST_VALUE_HELLO, ctx.getValueFromContext(RESULT_2_PATH));
    assertEquals(ctx.getValueFromContext(RESULT_2_PATH), ctx.getValueFromContext(RESULT_PATH));
  }

  @Test
  @Order(20)
  void exceptionThrown() {
    InvocationRequest request = testInvocationRequest("nonExistentMethod")
        .addParametersItem(new InvocationParameter()
            .name("input").typeClass(Object.class.getName()).value("fail"));

    InvocationRun run = new InvocationRunBuilder()
        .addItem(InvocationRunItemBuilder.withRequest(request)
            .throwException(true))
        .build();

    assertThrows(IllegalArgumentException.class, () -> invocationApi.run(ctx, run));
  }

  @Test
  @Order(21)
  void exceptionSuppressed() {
    InvocationRequest request = testInvocationRequest("nonExistentMethod")
        .addParametersItem(new InvocationParameter()
            .name("input").typeClass(Object.class.getName()).value("fail"));

    InvocationRun run = new InvocationRunBuilder()
        .addItem(InvocationRunItemBuilder.withRequest(request)
            .throwException(false))
        .build();

    // no exception should escape
    assertDoesNotThrow(() -> invocationApi.run(ctx, run));
  }

  @Test
  @Order(30)
  void testParallelRun_threeSeparateParallels() {
    // Initialize context
    ctx.set("parallelResults", new HashMap<String, Object>());

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> {
          // First parallel
          item.addParallel(rb -> rb.addItem(ib -> ib
              .request(testInvocationRequest("invocationReturnInput")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Object.class.getName()).value("first")))
              .applyResult(List.of("parallelResults", "first"),
                  List.of(ContextObject.INVOCATION_RESULT))));
          // Second parallel
          item.addParallel(rb -> rb.addItem(ib -> ib
              .request(testInvocationRequest("invocationReturnInput")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Object.class.getName()).value("second")))
              .applyResult(List.of("parallelResults", "second"),
                  List.of(ContextObject.INVOCATION_RESULT))));
          // Third parallel
          item.addParallel(rb -> rb.addItem(ib -> ib
              .request(testInvocationRequest("invocationReturnInput")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Object.class.getName()).value("third")))
              .applyResult(List.of("parallelResults", "third"),
                  List.of(ContextObject.INVOCATION_RESULT))));
        })
        .build();

    invocationApi.run(ctx, run);
    Map<String, Object> expectedResults = new HashMap<>();
    expectedResults.put("first", "first");
    expectedResults.put("second", "second");
    expectedResults.put("third", "third");
    assertEquals("first", ctx.getValueFromContext(List.of("parallelResults", "first")));
    assertEquals("second", ctx.getValueFromContext(List.of("parallelResults", "second")));
    assertEquals("third", ctx.getValueFromContext(List.of("parallelResults", "third")));
    assertEquals(expectedResults, ctx.getValueFromContext(List.of("parallelResults")));
  }

  @Test
  @Order(40)
  void testConditionals_trueBranchExecuted() {
    ctx.set("resultMap", new HashMap<>());

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> item.conditional(
            pb -> pb.expression("true"),
            rb -> rb.addItem(ib -> ib.request(testInvocationRequest("invocationReturnInput")
                .addParametersItem(new InvocationParameter()
                    .name("input").typeClass(Object.class.getName()).value("condTrue")))
                .applyResult(List.of("resultMap", "result"),
                    List.of(ContextObject.INVOCATION_RESULT))))
            .setElse(rb -> rb.addItem(ib -> ib
                .request(testInvocationRequest("invocationReturnInput")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Object.class.getName()).value("condFalse")))
                .applyResult(List.of("resultMap", "result"),
                    List.of(ContextObject.INVOCATION_RESULT)))))
        .build();

    invocationApi.run(ctx, run);

    assertEquals("condTrue", ctx.getValueFromContext(List.of("resultMap", "result")));
  }

  @Test
  @Order(41)
  void testConditionals_falseBranchElseExecuted() {
    ctx.set("resultMap", new HashMap<>());

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> item.conditional(
            pb -> pb.expression("false"),
            rb -> rb.addItem(ib -> ib.request(testInvocationRequest("invocationReturnInput")
                .addParametersItem(new InvocationParameter()
                    .name("input").typeClass(Object.class.getName()).value("condTrue")))
                .applyResult(List.of("resultMap", "result"),
                    List.of(ContextObject.INVOCATION_RESULT))))
            .setElse(rb -> rb.addItem(ib -> ib
                .request(testInvocationRequest("invocationReturnInput")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Object.class.getName()).value("condFalse")))
                .applyResult(List.of("resultMap", "result"),
                    List.of(ContextObject.INVOCATION_RESULT)))))
        .build();

    invocationApi.run(ctx, run);

    assertEquals("condFalse", ctx.getValueFromContext(List.of("resultMap", "result")));
  }

  // FIXME enable once evaluate gets implemented in InvocationApiImpl
  @Test
  // @Disabled
  @Order(50)
  void testWhileLoop_runsUntilPredicateFalse() {
    // initialize counter to 0
    ctx.setValue(COUNTER_PATH, 0, true);
    ctx.setValue(COUNTER_LESS_THAN_3_PATH, true, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> item.setWhile(
            pb -> pb.fromPath(COUNTER_LESS_THAN_3_PATH),
            rb -> rb.addItem(InvocationRunItemBuilder
                .withRequest(testInvocationRequest("invocationIncrementCounter")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Integer.class.getName())))
                .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT)))
                .addItem(InvocationRunItemBuilder
                    .withRequest(testInvocationRequest("invocationCounterBelowThree")
                        .addParametersItem(new InvocationParameter()
                            .name("input").typeClass(Integer.class.getName())))
                    .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                    .applyResult(COUNTER_LESS_THAN_3_PATH,
                        List.of(ContextObject.INVOCATION_RESULT)))))
        .build();

    invocationApi.run(ctx, run);

    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    assertTrue(counter >= 3, "Counter should be >= 3 after while loop");
  }

  // FIXME enable once evaluate gets implemented in InvocationApiImpl
  @Test
  // @Disabled
  @Order(52)
  void testDoWhileLoop_executesOnceEvenIfPredicateFalseInitially() {
    // initialize counter to 5 so "counter < 3" is false at the start
    ctx.setValue(COUNTER_PATH, 5, true);
    ctx.setValue(COUNTER_LESS_THAN_3_PATH, false, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> item.setDoWhile(
            pb -> pb.fromPath(COUNTER_LESS_THAN_3_PATH),
            rb -> rb
                // increment counter
                .addItem(ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Integer.class.getName())))
                    .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                    .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT)))
                // evaluate predicate counter < 3
                .addItem(ib -> ib.request(testInvocationRequest("invocationCounterBelowThree")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Integer.class.getName())))
                    .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                    .applyResult(COUNTER_LESS_THAN_3_PATH,
                        List.of(ContextObject.INVOCATION_RESULT)))))
        .build();

    invocationApi.run(ctx, run);

    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    assertEquals(6, counter,
        "Do-while should execute body once even though predicate was false initially");
  }

  // FIXME enable once evaluate gets implemented in InvocationApiImpl
  @Test
  // @Disabled
  @Order(53)
  void testWhileLoop_skippedWhenPredicateFalseInitially() {
    // initialize counter to 5 so "counter < 3" is false
    ctx.setValue(COUNTER_PATH, 5, true);
    ctx.setValue(COUNTER_LESS_THAN_3_PATH, false, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> item.setWhile(
            pb -> pb.fromPath(COUNTER_LESS_THAN_3_PATH),
            rb -> rb
                // increment counter
                .addItem(ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Integer.class.getName())))
                    .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                    .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT)))
                // evaluate predicate counter < 3
                .addItem(ib -> ib.request(testInvocationRequest("invocationCounterBelowThree")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Integer.class.getName())))
                    .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                    .applyResult(COUNTER_LESS_THAN_3_PATH,
                        List.of(ContextObject.INVOCATION_RESULT)))))
        .build();

    invocationApi.run(ctx, run);

    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    assertEquals(5, counter, "Counter should remain unchanged because while loop did not execute");
  }

  // Complex tests:
  @Test
  @Order(100)
  void testWhileLoop_withNestedConditional() {
    ctx.setValue(COUNTER_PATH, 0, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> item.setWhile(
            pb -> pb.expression("#testMap.get('counter') < 3"),
            rb -> rb
                // increment counter
                .addItem(ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Integer.class.getName())))
                    .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                    .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT)))
                // conditional inside while
                .addItem(inner -> inner.conditional(
                    pb -> pb.expression("#testMap.get('counter') % 2 == 0"),
                    rb2 -> rb2.addItem(ib -> ib
                        .request(testInvocationRequest("invocationReturnInput")
                            .addParametersItem(new InvocationParameter()
                                .name("input").typeClass(Object.class.getName()).value("even")))
                        .applyResult(List.of(TEST_MAP, "lastParity"),
                            List.of(ContextObject.INVOCATION_RESULT))))
                    .setElse(rb2 -> rb2.addItem(ib -> ib
                        .request(testInvocationRequest("invocationReturnInput")
                            .addParametersItem(new InvocationParameter()
                                .name("input").typeClass(Object.class.getName()).value("odd")))
                        .applyResult(List.of(TEST_MAP, "lastParity"),
                            List.of(ContextObject.INVOCATION_RESULT)))))))
        .build();

    invocationApi.run(ctx, run);

    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    String lastParity = (String) ctx.getValueFromContext(List.of(TEST_MAP, "lastParity"));

    assertTrue(counter >= 3, "Loop should stop when counter >= 3");
    assertTrue("even".equals(lastParity) || "odd".equals(lastParity),
        "Nested conditional should set lastParity to even/odd");
  }

  // @Disabled
  @Test
  @Order(109)
  void testParallel_sameItem() {
    // Initialize counter and predicate
    ctx.setValue(COUNTER_PATH, 0, true);
    ctx.setValue(COUNTER_LESS_THAN_3_PATH, true, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(parallelItem -> {
          // First parallel
          parallelItem.addParallel(rb1 -> rb1.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));

          // Second parallel
          parallelItem.addParallel(rb2 -> rb2.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));

          // Third parallel
          parallelItem.addParallel(rb3 -> rb3.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));
        })
        // Evaluate predicate for while loop
        .addItem(ib -> ib.request(testInvocationRequest("invocationCounterBelowThree")
            .addParametersItem(new InvocationParameter()
                .name("input").typeClass(Integer.class.getName())))
            .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
            .applyResult(COUNTER_LESS_THAN_3_PATH,
                List.of(ContextObject.INVOCATION_RESULT)))
        .build();

    invocationApi.run(ctx, run);

    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    // assertEquals(3, counter,
    // "While loop with nested parallel should increment counter until at least 3");
  }

  @Disabled
  @Test
  @Order(110)
  void testWhileLoop_withNestedParallel() {
    // Initialize counter and predicate
    ctx.setValue(COUNTER_PATH, 0, true);
    ctx.setValue(COUNTER_LESS_THAN_3_PATH, true, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> item.setWhile(
            pb -> pb.fromPath(COUNTER_LESS_THAN_3_PATH),
            rb -> rb
                // Add a parallel run with 3 separate parallel tasks
                .addItem(parallelItem -> {
                  // First parallel
                  parallelItem.addParallel(rb1 -> rb1.addItem(
                      ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                          .addParametersItem(new InvocationParameter()
                              .name("input").typeClass(Integer.class.getName())))
                          .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                          .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));

                  // Second parallel
                  parallelItem.addParallel(rb2 -> rb2.addItem(
                      ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                          .addParametersItem(new InvocationParameter()
                              .name("input").typeClass(Integer.class.getName())))
                          .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                          .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));

                  // Third parallel
                  parallelItem.addParallel(rb3 -> rb3.addItem(
                      ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                          .addParametersItem(new InvocationParameter()
                              .name("input").typeClass(Integer.class.getName())))
                          .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                          .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));
                })
                // Evaluate predicate for while loop
                .addItem(ib -> ib.request(testInvocationRequest("invocationCounterBelowThree")
                    .addParametersItem(new InvocationParameter()
                        .name("input").typeClass(Integer.class.getName())))
                    .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                    .applyResult(COUNTER_LESS_THAN_3_PATH,
                        List.of(ContextObject.INVOCATION_RESULT)))))
        .build();

    invocationApi.run(ctx, run);

    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    assertEquals(3, counter,
        "While loop with nested parallel should increment counter until at least 3");
  }

  @Test
  @Order(111)
  void testParallel_withNestedWhileLoops() {
    // Initialize counters for each parallel loop
    ctx.set("resultMap", new HashMap<String, Object>());
    ctx.setValue(List.of("resultMap", "counter1"), 0, true);
    ctx.setValue(List.of("resultMap", "counter1LessThan3"), true, true);
    ctx.setValue(List.of("resultMap", "counter2"), 0, true);
    ctx.setValue(List.of("resultMap", "counter2LessThan3"), true, true);
    ctx.setValue(List.of("resultMap", "counter3"), 0, true);
    ctx.setValue(List.of("resultMap", "counter3LessThan3"), true, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(parallelItem -> {
          // First parallel → while loop updating resultMap.counter1
          parallelItem.addParallel(rb1 -> rb1.addItem(item -> item.setWhile(
              pb -> pb.fromPath(List.of("resultMap", "counter1LessThan3")),
              rb -> rb
                  .addItem(ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                      .addParametersItem(new InvocationParameter()
                          .name("input").typeClass(Integer.class.getName())))
                      .addResolver(mb -> mb.addMapping(
                          map -> map.fromPath(List.of("resultMap", "counter1"))))
                      .applyResult(List.of("resultMap", "counter1"),
                          List.of(ContextObject.INVOCATION_RESULT)))
                  .addItem(ib -> ib.request(testInvocationRequest("invocationCounterBelowThree")
                      .addParametersItem(new InvocationParameter()
                          .name("input").typeClass(Integer.class.getName())))
                      .addResolver(mb -> mb.addMapping(
                          map -> map.fromPath(List.of("resultMap", "counter1"))))
                      .applyResult(List.of("resultMap", "counter1LessThan3"),
                          List.of(ContextObject.INVOCATION_RESULT))))));

          // Second parallel → while loop updating resultMap.counter2
          parallelItem.addParallel(rb2 -> rb2.addItem(item -> item.setWhile(
              pb -> pb.fromPath(List.of("resultMap", "counter2LessThan3")),
              rb -> rb
                  .addItem(ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                      .addParametersItem(new InvocationParameter()
                          .name("input").typeClass(Integer.class.getName())))
                      .addResolver(mb -> mb.addMapping(
                          map -> map.fromPath(List.of("resultMap", "counter2"))))
                      .applyResult(List.of("resultMap", "counter2"),
                          List.of(ContextObject.INVOCATION_RESULT)))
                  .addItem(ib -> ib.request(testInvocationRequest("invocationCounterBelowThree")
                      .addParametersItem(new InvocationParameter()
                          .name("input").typeClass(Integer.class.getName())))
                      .addResolver(mb -> mb.addMapping(
                          map -> map.fromPath(List.of("resultMap", "counter2"))))
                      .applyResult(List.of("resultMap", "counter2LessThan3"),
                          List.of(ContextObject.INVOCATION_RESULT))))));

          // Third parallel → while loop updating resultMap.counter3
          parallelItem.addParallel(rb3 -> rb3.addItem(item -> item.setWhile(
              pb -> pb.fromPath(List.of("resultMap", "counter3LessThan3")),
              rb -> rb
                  .addItem(ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                      .addParametersItem(new InvocationParameter()
                          .name("input").typeClass(Integer.class.getName())))
                      .addResolver(mb -> mb.addMapping(
                          map -> map.fromPath(List.of("resultMap", "counter3"))))
                      .applyResult(List.of("resultMap", "counter3"),
                          List.of(ContextObject.INVOCATION_RESULT)))
                  .addItem(ib -> ib.request(testInvocationRequest("invocationCounterBelowThree")
                      .addParametersItem(new InvocationParameter()
                          .name("input").typeClass(Integer.class.getName())))
                      .addResolver(mb -> mb.addMapping(
                          map -> map.fromPath(List.of("resultMap", "counter3"))))
                      .applyResult(List.of("resultMap", "counter3LessThan3"),
                          List.of(ContextObject.INVOCATION_RESULT))))));
        })
        .build();

    invocationApi.run(ctx, run);

    Integer counter1 = (Integer) ctx.getValueFromContext(List.of("resultMap", "counter1"));
    Integer counter2 = (Integer) ctx.getValueFromContext(List.of("resultMap", "counter2"));
    Integer counter3 = (Integer) ctx.getValueFromContext(List.of("resultMap", "counter3"));

    assertEquals(3, counter1, "Parallel 1 while loop should increment counter1 to 3");
    assertEquals(3, counter2, "Parallel 2 while loop should increment counter2 to 3");
    assertEquals(3, counter3, "Parallel 3 while loop should increment counter3 to 3");
  }

  @Test
  @Order(112)
  void testParallel_sharedCounterPath() {
    // Initialize shared counter at 0
    ctx.setValue(COUNTER_PATH, 0, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(parallelItem -> {
          // Three parallels all writing to the SAME resultMap.counter
          parallelItem.addParallel(rb1 -> rb1.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(
                      mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH,
                      List.of(ContextObject.INVOCATION_RESULT))));

          parallelItem.addParallel(rb2 -> rb2.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(
                      mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH,
                      List.of(ContextObject.INVOCATION_RESULT))));

          parallelItem.addParallel(rb3 -> rb3.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(
                      mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH,
                      List.of(ContextObject.INVOCATION_RESULT))));
        })
        .build();

    invocationApi.run(ctx, run);

    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    // TODO Currently last-writer-wins, what is the expected result?
    // assertEquals(3, counter, "Shared counter should be incremented by all parallel runs");
  }

  @Test
  @Order(120)
  void testParallel_withExceptionThrownInOneItem() {
    // Initialize counter value
    ctx.setValue(COUNTER_PATH, 0, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(parallelItem -> {
          // First parallel: increments normally
          parallelItem.addParallel(rb1 -> rb1.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));

          // Second parallel: fails (invalid invocation)
          parallelItem.addParallel(rb2 -> rb2.addItem(
              ib -> ib.request(testInvocationRequest("nonExistentMethod")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Object.class.getName()).value("boom")))
                  .throwException(true)));
        })
        .build();

    // Expect an exception to bubble up from the failing parallel
    assertThrows(IllegalArgumentException.class, () -> invocationApi.run(ctx, run));

    // The successful parallel should still have executed before the exception propagated
    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);

    // TODO Currently works based off of timing. What's the expected result?
    // assertEquals(0, counter,
    // "Counter should stay 0 because the run is stopped due to exception being thrown.");
  }

  @Test
  @Order(121)
  void testParallel_withExceptionSuppressedInOneItem() {
    // Initialize counter value
    ctx.setValue(COUNTER_PATH, 0, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(parallelItem -> {
          // First parallel: increments normally
          parallelItem.addParallel(rb1 -> rb1.addItem(
              ib -> ib.request(testInvocationRequest("invocationIncrementCounter")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Integer.class.getName())))
                  .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                  .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT))));

          // Second parallel: fails (invalid invocation)
          parallelItem.addParallel(rb2 -> rb2.addItem(
              ib -> ib.request(testInvocationRequest("nonExistentMethod")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Object.class.getName()).value("boom")))
                  .throwException(false)));
        })
        .build();

    // Expect an exception to bubble up from the failing parallel
    assertDoesNotThrow(() -> invocationApi.run(ctx, run));

    // The successful parallel should still have executed before the exception propagated
    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    assertTrue(counter >= 1,
        "Successful parallel should increment counter even if another parallel fails");
  }

  @Test
  @Order(122)
  void testParallel_withWhileAndSuppressedException() {
    // Initialize counter and predicate
    ctx.setValue(COUNTER_PATH, 0, true);
    ctx.setValue(COUNTER_LESS_THAN_3_PATH, true, true);

    InvocationRun run = new InvocationRunBuilder()
        .addItem(parallelItem -> {
          // Branch 1: while loop increments counter until < 3 is false
          parallelItem.addParallel(rb1 -> rb1.addItem(
              ib -> ib.setWhile(
                  pb -> pb.fromPath(COUNTER_LESS_THAN_3_PATH),
                  rb -> rb
                      // increment counter
                      .addItem(ib2 -> ib2
                          .request(testInvocationRequest("invocationIncrementCounter")
                              .addParametersItem(new InvocationParameter()
                                  .name("input").typeClass(Integer.class.getName())))
                          .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                          .applyResult(COUNTER_PATH, List.of(ContextObject.INVOCATION_RESULT)))
                      // re-evaluate predicate counter < 3
                      .addItem(ib2 -> ib2
                          .request(testInvocationRequest("invocationCounterBelowThree")
                              .addParametersItem(new InvocationParameter()
                                  .name("input").typeClass(Integer.class.getName())))
                          .addResolver(mb -> mb.addMapping(map -> map.fromPath(COUNTER_PATH)))
                          .applyResult(COUNTER_LESS_THAN_3_PATH,
                              List.of(ContextObject.INVOCATION_RESULT))))));

          // Branch 2: suppressed exception
          parallelItem.addParallel(rb2 -> rb2.addItem(
              ib -> ib.request(testInvocationRequest("nonExistentMethod")
                  .addParametersItem(new InvocationParameter()
                      .name("input").typeClass(Object.class.getName()).value("boom")))
                  .throwException(false)));
        })
        .build();

    // Run should complete without throwing, because exception is suppressed
    assertDoesNotThrow(() -> invocationApi.run(ctx, run));

    // Counter should eventually reach 3
    Integer counter = (Integer) ctx.getValueFromContext(COUNTER_PATH);
    assertEquals(3, counter,
        "While loop in branch 1 should increment counter to 3 even with suppressed errors in branch 2");
  }

  // Edge case tests:

  @Test
  @Order(200)
  void testEmptyInvocationRun_doesNothing() {
    InvocationRun run = new InvocationRunBuilder().build();

    assertDoesNotThrow(() -> invocationApi.run(ctx, run));
    // Context should remain empty → check that counter path is still unset
    assertThrows(IllegalArgumentException.class,
        () -> ctx.getValueFromContext(List.of(ContextObject.INVOCATION_RESULT)),
        "No values should be written to context when run has no items");
    assertTrue(ctx.getItemAsObject(TEST_MAP, Map.class).isEmpty());
  }

  @Test
  @Order(201)
  void testInvocationRunWithNullContext_throwsNPE() {
    InvocationRun run = new InvocationRunBuilder()
        .addItem(InvocationRunItemBuilder.withRequest(testInvocationRequest("invocationReturnInput")
            .addParametersItem(new InvocationParameter()
                .name("input").typeClass(Object.class.getName()).value(TEST_VALUE_HELLO))))
        .build();

    assertThrows(NullPointerException.class, () -> invocationApi.run(null, run));
  }

  @Test
  @Order(202)
  void testInvocationRunItemWithoutRequest_safeSkip() {
    InvocationRun run = new InvocationRunBuilder()
        .addItem(item -> {
          // Item with neither request nor loop/conditional, should be skipped
        })
        .build();

    assertDoesNotThrow(() -> invocationApi.run(ctx, run));
    // Verify that known paths remain unset
    assertThrows(IllegalArgumentException.class,
        () -> ctx.getValueFromContext(List.of(ContextObject.INVOCATION_RESULT)),
        "InvocationResult context object should not exist because no item executed");
    assertTrue(ctx.getItemAsObject(TEST_MAP, Map.class).isEmpty());
  }

  @Test
  @Order(203)
  void testInvocationRun_withNullInputParameter() {
    InvocationRun run = new InvocationRunBuilder()
        .addItem(InvocationRunItemBuilder.withRequest(testInvocationRequest("invocationReturnInput")
            .addParametersItem(new InvocationParameter()
                .name("input").typeClass(Object.class.getName()).value(null))))
        .build();

    assertDoesNotThrow(() -> invocationApi.run(ctx, run));
    // Verify that the null was actually passed through
    Object result = ctx.getValueFromContext(List.of(ContextObject.INVOCATION_RESULT));
    assertNull(result, "Invocation with null input should yield null result");
  }



  private InvocationRequest testInvocationRequest(String methodName) {
    return new InvocationRequest()
        .interfaceClass(TestRunHelperApi.class.getName()).name(TestRunHelperApi.class.getName())
        .methodName(methodName);
  }
}
