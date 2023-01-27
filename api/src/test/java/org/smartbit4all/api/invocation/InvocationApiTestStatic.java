package org.smartbit4all.api.invocation;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import org.junit.jupiter.api.Assertions;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.TestDataBean;
import org.smartbit4all.core.utility.StringConstant;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvocationApiTestStatic {

  static void testPrimary(InvocationApi invocationApi) throws Exception {
    {
      String value = "Peter";
      InvocationRequest request = Invocations.invoke(TestApi.class).name(TestApiImpl.NAME)
          .methodName("doMethod")
          .addParametersItem(
              new InvocationParameter().name("p1").value(value).typeClass(String.class.getName()));
      invocationApi.invoke(request);
      Assertions.assertEquals(value, TestApiImpl.lastDo);
    }
    {
      String value = "Peter";
      InvocationRequest request = Invocations.invoke(TestApi.class).name(TestApiImpl.NAME)
          .methodName("echoMethod")
          .addParametersItem(
              new InvocationParameter().name("p1").value(value).typeClass(String.class.getName()));
      InvocationParameter result = invocationApi.invoke(request);
      Assertions.assertEquals(value, result.getValue());
    }
  }

  static void testInvocationByTemplate(InvocationApi invocationApi) throws Exception {
    String value = "Peter";
    InvocationRequest request = TestApi.echoMethodTemplate(value);
    InvocationParameter result = invocationApi.invoke(request);
    Assertions.assertEquals(value, result.getValue());
  }

  static void testInvocationByBuilder(InvocationApi invocationApi) throws Exception {
    String value = "Peter";
    InvocationRequest request =
        invocationApi.builder(TestApi.class).build(a -> a.echoMethod(value));
    InvocationParameter result = invocationApi.invoke(request);
    Assertions.assertEquals(value, result.getValue());
  }

  static void testInvocationHandler(InvocationApi invocationApi, TestApi testApi) throws Exception {
    String value = "Peter";
    testApi.doMethod(value);
    Assertions.assertEquals(value, TestApiImpl.lastDo);

    String result = testApi.echoMethod(value);
    Assertions.assertEquals(value, result);
  }

  static void testInvokeAsync(InvocationApi invocationApi) throws Exception {
    String value = "Peter";
    InvocationRequest request =
        invocationApi.builder(TestApi.class).build(a -> a.setFutureValue(value));
    invocationApi.invokeAsync(request, InvocationTestConfig.GLOBAL_ASYNC_CHANNEL);
    Assertions.assertEquals(value, TestApi.futureValue.get(10, TimeUnit.SECONDS));
  }

  static void testInvokeAt(InvocationApi invocationApi) throws Exception {
    String value = "Peter";
    InvocationRequest request =
        invocationApi.builder(TestApi.class).build(a -> a.setFutureValueAt(value));

    for (int i = 0; i < TestApi.scheduleCounter; i++) {
      invocationApi.invokeAt(request, InvocationTestConfig.GLOBAL_ASYNC_CHANNEL,
          OffsetDateTime.now().plus(i * 2, ChronoUnit.SECONDS));
    }
    assertDoesNotThrow(
        () -> TestApi.scheduledLatch.await(TestApi.scheduleCounter * 2 + 5, TimeUnit.SECONDS));

  }

  static void testInvokeAsyncFlow(InvocationApi invocationApi) throws Exception {
    String value = "Peter";
    String second = " Second";
    String third = " Third";
    invocationApi
        .invoke(invocationApi.asyncBuilder()
            .call(TestApi.class, a -> a.firstStep(value), InvocationTestConfig.GLOBAL_ASYNC_CHANNEL)
            .evaluate(TestApi.class, a -> a.firstStepOnError(null, null))
            .andThen(TestApi.class, a -> a.secondStep(null, second),
                InvocationTestConfig.SECOND_ASYNC_CHANNEL)
            .andThenContinue(TestApi.class, a -> a.thirdStep(null, third),
                InvocationTestConfig.SECOND_ASYNC_CHANNEL)
            .evaluate(TestApi.class, a -> a.thirdStepOnError(null, null)).get());
    Assertions.assertEquals(value + second, TestApiImpl.secondResult.get());
    Assertions.assertEquals(value + third, TestApiImpl.thirdResult.get());
  }

  static void testSubscription(InvocationApi invocationApi,
      TestEventPublisherApi testEventPublisherApi) throws Exception {
    String value = "Peter";

    String event = testEventPublisherApi.fireSomeEvent(value);

    Assertions.assertEquals(event + StringConstant.COLON_SPACE + value,
        TestEventSubscriberApiImpl.eventResult.get());
  }

  static void testScriptCall(InvocationApi invocationApi) throws Exception {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("x", 2);
    vars.put("y", 1);
    vars.put("z", 3);
    System.out.println("result = " + engine.eval("x + y + z", new SimpleBindings(vars)));

    Assertions.assertEquals(6.0,
        engine.eval("x + y + z", new SimpleBindings(vars)));
  }

  static void testInvokeListAndMapParam(InvocationApi invocationApi, CollectionApi collectionApi)
      throws Exception {
    String value = "Peter";
    List<TestDataBean> list = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      list.add(new TestDataBean().data("data " + i));
    }

    assertThrows(IllegalArgumentException.class, () -> {
      InvocationRequest request =
          invocationApi.builder(TestApi.class).build(a -> a.modifyData(list));
    });

    InvocationRequest request =
        invocationApi.builder(TestApi.class)
            .build(a -> a.modifyData(Invocations.listOf(list, TestDataBean.class)));

    InvocationParameter result1 = invocationApi.invoke(request);

    // Save the request and reload.
    StoredReference<InvocationRequest> storedReference = collectionApi
        .reference(Invocations.INVOCATION_SCHEME, "myRequest", InvocationRequest.class);
    storedReference
        .set(request);

    InvocationRequest reloaderRequest = storedReference.get();

    InvocationParameter resultReloaded = invocationApi.invoke(reloaderRequest);

  }

}
