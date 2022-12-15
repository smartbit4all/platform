package org.smartbit4all.api.invocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    InvocationTestConfig.class,
})
class InvocationApiTest {

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private TestApi testApi;

  @BeforeAll
  public static void setUpBeforeClass(
      @Value("${applicationruntime.maintain.fixeddelay:5000}") String schedulePeriodString) {
    Long maintainDelay = Long.valueOf(schedulePeriodString);
    waitForRefresh(maintainDelay);
  }

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

  public static String callResult;

  @Test
  void testInvocationHandler() throws Exception {
    String value = "Peter";
    testApi.doMethod(value);
    Assertions.assertEquals(value, TestApiImpl.lastDo);

    String result = testApi.echoMethod(value);
    Assertions.assertEquals(value, result);
  }

  protected static void waitForRefresh(Long maintainDelay) {
    try {
      Thread.sleep(maintainDelay);
    } catch (InterruptedException e) {
    }
  }

}
