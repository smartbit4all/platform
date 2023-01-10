package org.smartbit4all.api.invocation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    InvocationTestConfig.class,
})
class InvocationApiTest {

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private TestApi testApi;

  @Autowired
  private TestEventPublisherApi testEventPublisherApi;

  @Test
  void testPrimary() throws Exception {
    InvocationApiTestStatic.testPrimary(invocationApi);
  }

  @Test
  void testInvocationByTemplate() throws Exception {
    InvocationApiTestStatic.testInvocationByTemplate(invocationApi);
  }

  @Test
  void testInvocationByBuilder() throws Exception {
    InvocationApiTestStatic.testInvocationByBuilder(invocationApi);
  }

  @Test
  void testInvocationHandler() throws Exception {
    InvocationApiTestStatic.testInvocationHandler(invocationApi, testApi);
  }

  @Test
  void testInvokeAsync() throws Exception {
    InvocationApiTestStatic.testInvokeAsync(invocationApi);
  }

  @Test
  void testInvokeAt() throws Exception {
    InvocationApiTestStatic.testInvokeAt(invocationApi);
  }

  @Test
  void testInvokeAsyncFlow() throws Exception {
    InvocationApiTestStatic.testInvokeAsyncFlow(invocationApi);
  }

  @Test
  void testSubscription() throws Exception {
    InvocationApiTestStatic.testSubscription(invocationApi, testEventPublisherApi);
  }

}
