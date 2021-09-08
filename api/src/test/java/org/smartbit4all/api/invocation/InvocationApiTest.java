package org.smartbit4all.api.invocation;

import static org.assertj.core.api.Assertions.fail;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.invocation.bean.TestDataBean;
import org.smartbit4all.domain.data.storage.ObjectReferenceRequest;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    InvocationTestConfig.class,
})
class InvocationApiTest {

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  TestPrimaryApi primaryApi;

  @Autowired
  StorageApi storageApi;

  @Test
  void testPrimary() throws ClassNotFoundException {
    {
      String value = "Peter";
      InvocationRequest request = Invocations.invoke(TestApi.class).method("doMethod")
          .addParameter("p1", value, String.class.getName());
      invocationApi.invoke(request);
      Assertions.assertEquals(value, TestApiImpl.lastDo);
    }
    {
      String value = "Peter";
      InvocationRequest request = Invocations.invoke(TestApi.class).method("echoMethod")
          .addParameter("p1", value, String.class.getName());
      InvocationParameter result = invocationApi.invoke(request);
      Assertions.assertEquals(value, result.getValue());
    }
  }

  @Test
  void testInvocationByTemplate() throws ClassNotFoundException {
    {
      String value = "Peter";
      InvocationRequest request =
          InvocationRequest.of(TestApi.echoMethodTemplate).setParameter("p1", value);
      InvocationParameter result = invocationApi.invoke(request);
      Assertions.assertEquals(value, result.getValue());
    }
  }

  public static String callResult;

  @Test
  void testEventPublishByObjectReference() throws Exception {

    callResult = null;

    // Save the invocationTemplate first:
    URI callbackUri = invocationApi.save(TestApi.echoMethodTemplate);
    String value = "echo";

    Storage<TestDataBean> storageTDB = storageApi.get(TestDataBean.class);

    TestDataBean tdb1 = new TestDataBean();
    tdb1.setData("tdb1");

    URI tdb1Uri = storageTDB.save(tdb1);
    storageTDB.saveReferences(new ObjectReferenceRequest(tdb1Uri, InvocationRequestTemplate.class)
        .add(callbackUri.toString()));

    storageApi.onChange(TestDataBean.class, InvocationRequestTemplate.class,
        (b, r) -> r.forEach(requestTemplate -> {
          InvocationRequest request =
              InvocationRequest.of(requestTemplate).setParameter("p1", value);
          InvocationParameter result;
          try {
            result = invocationApi.invoke(request);
            callResult = result.getValue().toString();
          } catch (ClassNotFoundException e) {
            fail("Unable to call the " + request, e);
          }
        }));

    // Modify the give data
    tdb1.setUri(tdb1Uri);
    tdb1.setData(value + value);
    storageTDB.save(tdb1);

    // Triggering by the modification the result will lead to invocation.
    Assertions.assertEquals(value, callResult);
  }

  @Test
  void testContribution() throws ClassNotFoundException {
    {
      String value = "Peter";
      InvocationRequest request =
          Invocations.invoke(TestPrimaryApi.class).innerApi("contributionApi1")
              .method("doSomething")
              .addParameter("p1", value, String.class.getName());
      invocationApi.invoke(request);
      Assertions.assertEquals(value, TestContributionApiImpl.lastDoSomething);
    }
    {
      String value = "Peter";
      InvocationRequest request =
          Invocations.invoke(TestPrimaryApi.class).innerApi("contributionApi1")
              .method("echoMethod")
              .addParameter("p1", value, String.class.getName());
      InvocationParameter result = invocationApi.invoke(request);
      Assertions.assertEquals(value, result.getValue());
    }
    {
      String value = "Joke";
      InvocationRequest request =
          Invocations.invoke(TestPrimaryApi.class).innerApi("contributionApi2")
              .method("doSomething")
              .addParameter("p1", value, String.class.getName());
      invocationApi.invoke(request);
      Assertions.assertEquals(value, TestContributionApiImpl.lastDoSomething);
    }
    {
      String value = "Peter";
      TestContributionApi testContributionApi = primaryApi.findApiByName("contributionApi1");
      testContributionApi.doSomething(value);
      Assertions.assertEquals(value, TestContributionApiImpl.lastDoSomething);
      Assertions.assertEquals(value, testContributionApi.echoMethod(value));
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
