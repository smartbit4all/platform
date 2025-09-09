package org.smartbit4all.api.invocation.restclient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectMappingDefinitionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = {DynamicRestCallerTestConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
@Disabled
public class DynamicRestCallerApiTest {

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;
  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private DynamicRestCallerApi dynamicRestCallerApi;

  private static final String TEST1_CONNECTION_NAME = "calendar";
  private static final String TEST1_CONNECTION_ENDPOINT = "http://calapi.inadiutorium.cz/";
  private static final String TEST1_PATH = "/api/v0/en/calendars/general-en/today";
  private static final HttpMethod TEST1_METHOD = HttpMethod.GET;

  private static final String TEST2_CONNECTION_NAME = "dadJoke";
  private static final String TEST2_CONNECTION_ENDPOINT = "https://icanhazdadjoke.com/";
  private static final HttpMethod TEST2_METHOD = HttpMethod.GET;
  private static final Map<String, Object> TEST2_PARAMS = Map.of("Accept", "application/json");

  private static final String RESTFUL_CONNECTION_NAME = "restfulDevApi";
  private static final String RESTFUL_CONNECTION_ENDPOINT = "https://api.restful-api.dev/";
  private static final String TEST3_PATH = "objects";
  private static final HttpMethod TEST3_METHOD = HttpMethod.POST;
  private static final String BODY_INPUT = "bodyInput";
  private static final Map<String, Object> TEST3_PARAMS =
      Map.of(BODY_INPUT, "hope this ends up in uppercase");

  @BeforeAll
  void setup() {

    // Add a service connection to meet with the api service connection.
    MDMEntryApi serviceConnectionEntry =
        masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            InvocationApiMdmConfig.MDM_ENTRY_SERVICECONNECTION);
    ServiceConnection serviceConnection =
        new ServiceConnection().name(TEST1_CONNECTION_NAME).endpoint(TEST1_CONNECTION_ENDPOINT);
    serviceConnectionEntry
        .save(objectApi.create("test",
            serviceConnection));

    ServiceConnection serviceConnection2 =
        new ServiceConnection().name(TEST2_CONNECTION_NAME).endpoint(TEST2_CONNECTION_ENDPOINT);
    serviceConnectionEntry
        .save(objectApi.create("test",
            serviceConnection2));
    ServiceConnection serviceConnection3 =
        new ServiceConnection().name(RESTFUL_CONNECTION_NAME).endpoint(RESTFUL_CONNECTION_ENDPOINT);
    serviceConnectionEntry
        .save(objectApi.create("test",
            serviceConnection3));
  }

  @Test
  void test1() {
    ResponseEntity<Object> responseEntity =
        dynamicRestCallerApi.callDynamicRest(TEST1_CONNECTION_NAME, TEST1_PATH, TEST1_METHOD, null,
            null, null, null);
    assertNotNull(responseEntity);
    assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    Object body = responseEntity.getBody();
  }

  @Test
  void test_null_path_on_endpoint() {
    ObjectMappingDefinition headerDef = ObjectMappingDefinitionBuilder.create()
        .addMapping(b -> b.fromPath(List.of("Accept")).to("Accept")).build();
    ResponseEntity<Object> responseEntity =
        dynamicRestCallerApi.callDynamicRest(TEST2_CONNECTION_NAME, null, TEST2_METHOD, null,
            headerDef, null, TEST2_PARAMS);
    assertNotNull(responseEntity);
    assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    Object body = responseEntity.getBody();
  }

  @Test
  void test_post() {
    ObjectMappingDefinition bodyDef = ObjectMappingDefinitionBuilder.create()
        .addMapping(b -> b.constant(
            "{\"name\": \"Apple MacBook Pro 16\", \"data\": {\"year\": 2019, \"price\": 1849.99, \"CPU model\": \"Intel Core i9\", \"Hard disk size\": \"1 TB\"}}"))
        .build();
    ResponseEntity<Object> responseEntity =
        dynamicRestCallerApi.callDynamicRest(RESTFUL_CONNECTION_NAME, TEST3_PATH, TEST3_METHOD,
            MediaType.APPLICATION_JSON,
            null, bodyDef, TEST3_PARAMS);
    assertNotNull(responseEntity);
    assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    Object body = responseEntity.getBody();
  }
}
