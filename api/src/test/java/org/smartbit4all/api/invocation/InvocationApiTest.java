package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.invocation.bean.TestDataBean;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    InvocationTestConfig.class,
})
class InvocationApiTest {

  private static final String TEST_DATA_SCHEMA = "testDataSchema";

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  TestPrimaryApi primaryApi;

  @Autowired
  StorageApi storageApi;

  @Test
  void testPrimary() throws Exception {
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
  void testInvocationByTemplate() throws Exception {
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

    Storage storage = storageApi.get(TEST_DATA_SCHEMA);

    TestDataBean tdb1 = new TestDataBean();
    tdb1.setData("tdb1");

    StorageObject<TestDataBean> soTdb1 = storage.instanceOf(TestDataBean.class);
    soTdb1.setObject(tdb1);
    soTdb1.setReference("myRef", new ObjectReference().uri(callbackUri));

    URI tdb1Uri = storage.save(soTdb1);

    StorageObject<TestDataBean> optTdb1 =
        storage.load(soTdb1.getUri(), TestDataBean.class);

    URI refUri = optTdb1.getReference("myRef").getReferenceData().getUri();

    Assertions.assertEquals(callbackUri, refUri);

    InvocationRequestTemplate requestTemplate =
        storage.read(refUri, InvocationRequestTemplate.class);

    Assertions.assertEquals(TestApi.echoMethodTemplate, requestTemplate);
  }

  @Test
  void testContribution() throws Exception {
    {
      String value = "Peter1";
      InvocationRequest request =
          Invocations.invoke(TestPrimaryApi.class).innerApi("contributionApi1")
              .method("doSomething")
              .addParameter("p1", value, String.class.getName());
      invocationApi.invoke(request);
      Assertions.assertEquals(value, TestContributionApiImpl.lastDoSomething);
    }
    {
      String value = "Peter2";
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
      String value = "Peter3";
      TestContributionApi testContributionApi = primaryApi.findApiByName("contributionApi1");
      testContributionApi.doSomething(value);
      Assertions.assertEquals(value, TestContributionApiImpl.lastDoSomething);
      Assertions.assertEquals(value, testContributionApi.echoMethod(value));
    }
  }

  private String text;

  void setText(String text) {
    this.text = text;
  }

  @Test
  void testInvokeApiInstanceLambda() throws Exception {

    text = StringConstant.EMPTY;
    TextConsumer textConsumer = this::setText;

    UUID uuid = invocationApi.register(textConsumer);
    String value = "apple";
    InvocationRequest request =
        Invocations.invoke(uuid).addParameter("text", value, String.class.getName());
    invocationApi.invoke(request);
    Assertions.assertEquals(value, text);

  }

  @Test
  void testInvokeApiInstanceInline() throws Exception {

    text = StringConstant.EMPTY;
    TextConsumer textConsumer = s -> setText(s);

    UUID uuid = invocationApi.register(textConsumer);
    String value = "apple";
    InvocationRequest request =
        Invocations.invoke(uuid).addParameter("text", value, String.class.getName());
    invocationApi.invoke(request);
    Assertions.assertEquals(value, text);

  }

  @Test
  void testInvokeApiInstanceAnonym() throws Exception {

    text = StringConstant.EMPTY;
    TextConsumer textConsumer = new TextConsumer() {

      @Override
      public void setText(String text) {
        InvocationApiTest.this.setText(text);
      }
    };

    UUID uuid = invocationApi.register(textConsumer);
    String value = "apple";
    InvocationRequest request =
        Invocations.invoke(uuid).addParameter("text", value, String.class.getName());
    invocationApi.invoke(request);
    Assertions.assertEquals(value, text);

  }

  @Test
  void testInvokeApiInstanceIF() throws Exception {

    text = StringConstant.EMPTY;
    ApiInstanceIF apiInstance = new ApiInstanceIF() {

      @Override
      public void setText(String text) {
        InvocationApiTest.this.setText(text);
      }

      @Override
      public String getText() {
        return text;
      }

      @Override
      public void clearText() {
        text = StringConstant.EMPTY;
      }
    };

    UUID uuid = invocationApi.register(apiInstance);
    String value = "apple";
    {
      InvocationRequest request =
          Invocations.invoke(uuid).method("setText").addParameter("text", value,
              String.class.getName());
      invocationApi.invoke(request);
    }
    Assertions.assertEquals(value, text);
    {
      String getTextResult;
      InvocationRequest request =
          Invocations.invoke(uuid).method("getText");
      InvocationParameter result = invocationApi.invoke(request);
      getTextResult = result.getStringValue();
      Assertions.assertEquals(getTextResult, text);
    }
    {
      InvocationRequest request =
          Invocations.invoke(uuid).method("clearText");
      invocationApi.invoke(request);
      Assertions.assertEquals(StringConstant.EMPTY, text);
    }

  }

  @Test
  void testRemoteRest() throws Exception {
    // String value = "Peter";
    // InvocationRequest request =
    // Invocations.invoke(TestApi.class).exec("mod-test-rest-remote").method("doMethod")
    // .parameter(InvocationParameterKind.PRIMITIVE, value, String.class.getName()).build();
    // invocationApi.invoke(request);
    // Assertions.assertEquals(value, TestApiImpl.lastDo);
  }

}
