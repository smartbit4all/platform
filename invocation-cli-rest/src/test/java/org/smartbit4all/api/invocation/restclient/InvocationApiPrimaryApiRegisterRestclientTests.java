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
public class InvocationApiPrimaryApiRegisterRestclientTests {

  private static final URI REMPTE_API_URI = ProviderApiInvocationHandler
      .uriOf(TestContributionApi.class, TestContributionApiImpl.NAME_REMOTE);

  private MockServerClient mockServerClient;

  @MockServerPort
  Integer mockServerPort;

  @Autowired
  private TestPrimaryApi testPrimaryApi;

  @Autowired
  private StorageApi storageApi;

  @Value("${applicationruntime.maintain.fixeddelay:5000}")
  private String schedulePeriodString;

  @BeforeAll
  public static void setUpBeforeClass(@Autowired StorageApi storageApi) throws IOException {

    Storage appRegistryStorage = storageApi.get(Invocations.APIREGISTRATION_SCHEME);
    ApiData apiData = new ApiData().interfaceName(TestContributionApi.class.getName())
        .name(TestContributionApiImpl.NAME_REMOTE).uri(REMPTE_API_URI);
    appRegistryStorage.saveAsNew(apiData);

    appRegistryStorage.update(InvocationRegisterApiIml.REGISTER_URI, ApiRegistryData.class, r -> {
      r.addApiListItem(apiData.getUri());
      return r;
    });
  }

  @Test
  void testPrimaryApiRegister() throws Exception {
    ApplicationRuntimeData runtimeData = new ApplicationRuntimeData().ipAddress("127.0.0.1")
        .serverPort(mockServerPort).uuid(UUID.randomUUID()).startupTime(System.currentTimeMillis())
        .timeOffset(0l).apis(Arrays.asList(REMPTE_API_URI));

    Long maintainDelay = Long.valueOf(schedulePeriodString);
    TestApplicationRuntime appRuntime = TestApplicationRuntime.create(storageApi)
        .runtimeOf(runtimeData).withMaintainDelay(maintainDelay);
    appRuntime.start();
    waitForRefresh();

    ObjectMapper objectMapper = new ObjectMapper();

    String returnTypeClass = String.class.getName();
    String returnValue = TestContributionApiImpl.NAME_REMOTE;
    InvocationParameter mockResponseData =
        new InvocationParameter().typeClass(returnTypeClass).value(returnValue);

    mockServerClient.when(request().withMethod("POST"))
        .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_JSON)
            .withBody(objectMapper.writeValueAsString(mockResponseData)));

    List<String> expectedValueWithRemote =
        Arrays.asList(TestContributionApiImpl.NAME, TestContributionApiImpl.NAME_REMOTE);

    List<String> expectedValueWithoutRemote =
        Arrays.asList(TestContributionApiImpl.NAME, TestContributionApiImpl.NAME_REMOTE);

    List<String> apis = testPrimaryApi.getApis();


    assertNotNull(apis);
    assertEquals(apis.size(), expectedValueWithRemote.size());
    assertTrue(
        apis.containsAll(expectedValueWithRemote) && expectedValueWithRemote.containsAll(apis));

    appRuntime.stop();
    waitForRefresh();

    apis = testPrimaryApi.getApis();

    assertNotNull(apis);
    assertTrue(apis.size() == expectedValueWithoutRemote.size());
    assertTrue(apis.containsAll(expectedValueWithoutRemote)
        && expectedValueWithoutRemote.containsAll(apis));

    appRuntime.start();
    waitForRefresh();

    apis = testPrimaryApi.getApis();

    assertNotNull(apis);
    assertEquals(apis.size(), expectedValueWithRemote.size());
    assertTrue(
        apis.containsAll(expectedValueWithRemote) && expectedValueWithRemote.containsAll(apis));

  }

  private void waitForRefresh() throws NumberFormatException, InterruptedException {
    Thread.sleep(Long.valueOf(schedulePeriodString));
  }
}
