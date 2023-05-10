package org.smartbit4all.api.invocation;

import org.junit.jupiter.api.Test;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.core.object.ObjectApi;
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

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

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

  void testScriptCall() throws Exception {
    InvocationApiTestStatic.testScriptCall(invocationApi);
  }

  @Test
  void testInvokeListAndMapParam() throws Exception {
    InvocationApiTestStatic.testInvokeListAndMapParam(invocationApi, collectionApi);
  }

  @Test
  void testInvokeByDefinition() throws Exception {
    InvocationApiTestStatic.testInvokeByDefinition(invocationApi, collectionApi, objectApi);
  }

  @Test
  void testInvokeBatchForObjects() throws Exception {
    InvocationApiTestStatic.testInvokeBatchForObjects(invocationApi, collectionApi, objectApi);
  }

}
