package org.smartbit4all.api.invocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    InvocationTestConfig.class,
})
class InvocationApiTest {

  @Autowired
  private InvocationApi invocationApi;

  @Test
  void testPrimary() throws ClassNotFoundException {
    String value = "Peter";
    InvocationRequest request = Invocations.invoke(TestApi.class).method("doMethod")
        .parameter(InvocationParameterKind.PRIMITIVE, value, String.class.getName()).build();
    invocationApi.invoke(request);
    Assertions.assertEquals(value, TestApiImpl.lastDo);
  }

  @Test
  void testContribution() throws ClassNotFoundException {
    {
      String value = "Peter";
      InvocationRequest request =
          Invocations.invoke(TestPrimaryApi.class).innerApi("contributionApi1")
              .method("doSomething")
              .parameter(InvocationParameterKind.PRIMITIVE, value, String.class.getName()).build();
      invocationApi.invoke(request);
      Assertions.assertEquals(value, TestContributionApiImpl.lastDoSomething);
    }
    {
      String value = "Joke";
      InvocationRequest request =
          Invocations.invoke(TestPrimaryApi.class).innerApi("contributionApi2")
              .method("doSomething")
              .parameter(InvocationParameterKind.PRIMITIVE, value, String.class.getName()).build();
      invocationApi.invoke(request);
      Assertions.assertEquals(value, TestContributionApiImpl.lastDoSomething);
    }
  }

  @Test
  void testRemoteRest() throws ClassNotFoundException {
    // String value = "Peter";
    // InvocationRequest request =
    // Invocations.invoke(TestApi.class).exec("mod-test-rest-remote").method("doMethod")
    // .parameter(InvocationParameterKind.PRIMITIVE, value, String.class.getName()).build();
    // invocationApi.invoke(request);
    // Assertions.assertEquals(value, TestApiImpl.lastDo);
  }

}
