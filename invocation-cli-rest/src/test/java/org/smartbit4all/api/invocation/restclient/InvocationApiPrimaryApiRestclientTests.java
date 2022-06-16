package org.smartbit4all.api.invocation.restclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerPort;
import org.mockserver.springtest.MockServerTest;
import org.smartbit4all.api.invocation.InvocationRegisterApiIml;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.invocation.TestContributionApi;
import org.smartbit4all.api.invocation.TestContributionApiImpl;
import org.smartbit4all.api.invocation.TestPrimaryApi;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.ApplicationRuntimeData;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.domain.application.TestApplicationRuntime;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@MockServerTest()
@SpringBootTest(classes = {InvocationApiPrimaryApiRestclientTestConfig.class}, properties = {})
public class InvocationApiPrimaryApiRestclientTests {

  private MockServerClient mockServerClient;

  @MockServerPort
  Integer mockServerPort;

  @Autowired
  private TestPrimaryApi testPrimaryApi;

  @BeforeAll
  public static void setUpBeforeClass(@Autowired StorageApi storageApi,
      @Value("${mockServerPort}") Integer mockServerPort,
      @Value("${applicationruntime.maintain.fixeddelay:5000}") String schedulePeriodString)
      throws IOException {

    URI uri = ProviderApiInvocationHandler.uriOf(TestContributionApi.class,
        TestContributionApiImpl.NAME_REMOTE);
    ApplicationRuntimeData runtimeData = new ApplicationRuntimeData().ipAddress("127.0.0.1")
        .serverPort(mockServerPort).uuid(UUID.randomUUID()).startupTime(System.currentTimeMillis())
        .timeOffset(0l).apis(Arrays.asList(uri));

    Long maintainDelay = Long.valueOf(schedulePeriodString);
    TestApplicationRuntime.create(storageApi).runtimeOf(runtimeData)
        .withMaintainDelay(maintainDelay).start();

    Storage appRegistryStorage = storageApi.get(Invocations.APIREGISTRATION_SCHEME);
    ApiData apiData = new ApiData().interfaceName(TestContributionApi.class.getName())
        .name(TestContributionApiImpl.NAME_REMOTE).uri(uri);
    appRegistryStorage.saveAsNew(apiData);

    appRegistryStorage.update(InvocationRegisterApiIml.REGISTER_URI, ApiRegistryData.class, r -> {
      r.addApiListItem(apiData.getUri());
      return r;
    });
    waitForRefresh(maintainDelay);
  }

  @Test
  void testPrimaryApiRegister() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    // getName request
    String returnTypeClass = String.class.getName();
    String returnValue = TestContributionApiImpl.NAME_REMOTE;
    InvocationParameter mockResponseData =
        new InvocationParameter().typeClass(returnTypeClass).value(returnValue);

    mockServerClient.when(request().withMethod("POST"))
        .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_JSON)
            .withBody(objectMapper.writeValueAsString(mockResponseData)));

    List<String> expectedValue =
        Arrays.asList(TestContributionApiImpl.NAME, TestContributionApiImpl.NAME_REMOTE);

    List<String> apis = testPrimaryApi.getApis();

    assertNotNull(apis);
    assertTrue(apis.size() == expectedValue.size());
    assertTrue(apis.containsAll(expectedValue) && expectedValue.containsAll(apis));
  }

  @Test
  void testPrimaryApiCall() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    // getName request
    String returnTypeClass = String.class.getName();
    String returnValue = "thisIsTheReturnValue ";

    InvocationParameter mockResponseData =
        new InvocationParameter().typeClass(returnTypeClass).value(returnValue);

    mockServerClient.when(request().withMethod("POST"))
        .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_JSON)
            .withBody(objectMapper.writeValueAsString(mockResponseData)));

    String p1 = "teszt";
    String remoteResult = testPrimaryApi.echoMethod(TestContributionApiImpl.NAME_REMOTE, p1);

    assertNotNull(remoteResult);
    assertEquals(returnValue, remoteResult);

    String localResult = testPrimaryApi.echoMethod(TestContributionApiImpl.NAME, p1);

    assertNotNull(localResult);
    assertEquals(p1, localResult);

  }

  protected static void waitForRefresh(Long maintainDelay) {
    try {
      Thread.sleep(maintainDelay);
    } catch (InterruptedException e) {
    }
  }
}
