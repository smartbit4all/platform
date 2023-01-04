package org.smartbit4all.api.invocation.restclient;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerPort;
import org.mockserver.springtest.MockServerTest;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationRegisterApiIml;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.invocation.TestApi;
import org.smartbit4all.api.invocation.TestApiImpl;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.TestDataBean;
import org.smartbit4all.domain.application.TestApplicationRuntime;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MockServerTest()
@SpringBootTest(classes = {InvocationApiRestclientTestConfig.class}, properties = {})
public class InvocationApiRestclientTests {

  private MockServerClient mockServerClient;

  @MockServerPort
  Integer mockServerPort;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private TestApi testApiInvocationHandler;

  @BeforeAll
  public static void setUpBeforeClass(@Autowired StorageApi storageApi,
      @Value("${mockServerPort}") Integer mockServerPort,
      @Value("${applicationruntime.maintain.fixeddelay:5000}") String schedulePeriodString)
      throws IOException {

    URI uri = ProviderApiInvocationHandler.uriOf(TestApi.class, TestApiImpl.NAME);
    ApplicationRuntimeData runtimeData = new ApplicationRuntimeData().ipAddress("127.0.0.1")
        .serverPort(mockServerPort).uuid(UUID.randomUUID()).startupTime(System.currentTimeMillis())
        .timeOffset(0l).apis(Arrays.asList(uri));

    Long maintainDelay = Long.valueOf(schedulePeriodString);
    TestApplicationRuntime.create(storageApi).runtimeOf(runtimeData)
        .withMaintainDelay(maintainDelay).start();

    Storage appRegistryStorage = storageApi.get(Invocations.APIREGISTRATION_SCHEME);
    ApiData apiData =
        new ApiData().interfaceName(TestApi.class.getName()).name(TestApiImpl.NAME).uri(uri);
    appRegistryStorage.saveAsNew(apiData);

    appRegistryStorage.update(InvocationRegisterApiIml.REGISTER_URI, ApiRegistryData.class, r -> {
      r.addApiListItem(apiData.getUri());
      return r;
    });
  }

  @Test
  void testRestCall() throws Exception {
    String returnTypeClass = String.class.getName();
    String returnValue = "thisIsTheReturnValue";

    InvocationParameter mockResponseData =
        new InvocationParameter().typeClass(returnTypeClass).value(returnValue);

    String mockResponse = new ObjectMapper().writeValueAsString(mockResponseData);

    mockServerClient.when(request().withMethod("POST")).respond(response().withStatusCode(200)
        .withContentType(MediaType.APPLICATION_JSON).withBody(mockResponse));

    InvocationRequest request = new InvocationRequest()
        .interfaceClass(TestApi.class.getName())
        .name(TestApiImpl.NAME).methodName("testMethod")
        .addParametersItem(
            new InvocationParameter()
                .name("param1")
                .value("this-is-a-string-param-at-param1"))
        .addParametersItem(
            new InvocationParameter()
                .name("param2")
                .value(222l));

    InvocationParameter returnParam = invocationApi.invoke(request);

    assertNotNull(returnParam);
    assertEquals(returnTypeClass, returnParam.getTypeClass());
    assertEquals(returnValue, returnParam.getValue());
  }

  @Test
  void testRestCallWithRemoteHandler() throws Exception {
    String returnTypeClass = String.class.getName();
    String returnValue = "thisIsTheReturnValue";

    InvocationParameter mockResponseData =
        new InvocationParameter().typeClass(returnTypeClass).value(returnValue);

    String mockResponse = new ObjectMapper().writeValueAsString(mockResponseData);


    mockServerClient.when(request().withMethod("POST")).respond(response().withStatusCode(200)
        .withContentType(MediaType.APPLICATION_JSON).withBody(mockResponse));

    String returnParam = testApiInvocationHandler.echoMethod("teszt");

    assertNotNull(returnParam);
    assertEquals(returnValue, returnParam);
  }

  @Test
  void testRestCallWithObjectParam() throws Exception {
    String returnTypeClass = TestDataBean.class.getName();
    TestDataBean paramValue = new TestDataBean().data("thisIsTheParamValue").bool(true);
    TestDataBean returnValue = new TestDataBean().data("thisIsTheReturnValue").bool(true);

    InvocationParameter mockResponseData =
        new InvocationParameter().typeClass(returnTypeClass).value(returnValue);

    String mockResponse = new ObjectMapper().writeValueAsString(mockResponseData);

    mockServerClient.when(request().withMethod("POST")).respond(response().withStatusCode(200)
        .withContentType(MediaType.APPLICATION_JSON).withBody(mockResponse));

    TestDataBean returnParam = testApiInvocationHandler.modifyData(paramValue);

    assertNotNull(returnParam);
    assertEquals(returnValue.getData(), returnParam.getData());
    assertEquals(returnValue.isBool(), returnParam.isBool());
  }
}
