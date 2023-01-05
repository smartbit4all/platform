package org.smartbit4all.api.invocation;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = {
    InvocationTestConfig.class,
})
class InvocationApiTest {

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private TestApi testApi;

  @Test
  void testPrimary() throws Exception {
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

  @Test
  void testInvocationByTemplate() throws Exception {
    String value = "Peter";
    InvocationRequest request = TestApi.echoMethodTemplate(value);
    InvocationParameter result = invocationApi.invoke(request);
    Assertions.assertEquals(value, result.getValue());
  }

  @Test
  void testInvocationByBuilder() throws Exception {
    String value = "Peter";
    InvocationRequest request =
        invocationApi.builder(TestApi.class).build(a -> a.echoMethod(value));
    InvocationParameter result = invocationApi.invoke(request);
    Assertions.assertEquals(value, result.getValue());
  }

  @Test
  void testInvocationHandler() throws Exception {
    String value = "Peter";
    testApi.doMethod(value);
    Assertions.assertEquals(value, TestApiImpl.lastDo);

    String result = testApi.echoMethod(value);
    Assertions.assertEquals(value, result);
  }

  @Test
  void testInvokeAsync() throws Exception {
    String value = "Peter";
    InvocationRequest request =
        invocationApi.builder(TestApi.class).build(a -> a.setFutureValue(value));
    invocationApi.invokeAsync(request, InvocationTestConfig.GLOBAL_ASYNC_CHANNEL);
    Assertions.assertEquals(value, TestApi.futureValue.get(10, TimeUnit.SECONDS));
  }

  @Test
  void testInvokeAt() throws Exception {
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

  @Test
  void testInvokeAsyncFlow() throws Exception {
    String value = "Peter";
    String second = " Second";
    String third = " Third";
    invocationApi
        .invoke(invocationApi.asyncBuilder()
            .call(TestApi.class, a -> a.firstStep(value), InvocationTestConfig.GLOBAL_ASYNC_CHANNEL)
            .evaluate(TestApi.class, a -> a.firstStepOnError(null))
            .andThen(TestApi.class, a -> a.secondStep(null, second),
                InvocationTestConfig.SECOND_ASYNC_CHANNEL)
            .andThenContinue(TestApi.class, a -> a.thirdStep(null, third),
                InvocationTestConfig.SECOND_ASYNC_CHANNEL)
            .evaluate(TestApi.class, a -> a.thirdStepOnError(null)).get());
    Assertions.assertEquals(value + second, TestApiImpl.secondResult.get());
    Assertions.assertEquals(value + third, TestApiImpl.thirdResult.get());
  }

}
