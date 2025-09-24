package org.smartbit4all.api.invocation.restclient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.invocation.bean.MethodTemplate;
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
  private InvocationApi invocationApi;
  @Autowired
  private DynamicRestCallerApi dynamicRestCallerApi;

  private static final String TEST1_CONNECTION_NAME = "calendar";
  private static final String TEST1_CONNECTION_ENDPOINT = "http://calapi.inadiutorium.cz/";
  private static final String TEST1_PATH = "/api/v0/en/calendars/general-en/today";
  private static final String TEST1_METHOD = HttpMethod.GET.toString();

  private static final String TEST2_CONNECTION_NAME = "dadJoke";
  private static final String TEST2_CONNECTION_ENDPOINT = "https://icanhazdadjoke.com/";
  private static final String TEST2_METHOD = HttpMethod.GET.toString();
  private static final Map<String, Object> TEST2_PARAMS = Map.of("Accept", "application/json");

  private static final String RESTFUL_CONNECTION_NAME = "restfulDevApi";
  private static final String RESTFUL_CONNECTION_ENDPOINT = "https://api.restful-api.dev/";
  private static final String TEST3_PATH = "objects";
  private static final String TEST3_METHOD = HttpMethod.POST.toString();
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

    prepareMethodTemplate();
  }

  void prepareMethodTemplate() {
    MDMEntryApi methodTemplateEntry =
        masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            InvocationApiMdmConfig.MDM_ENTRY_METHOD_TEMPLATE);

    MethodTemplate template = new MethodTemplate().fullyQualifiedName("kiscica.teszt")
        .requestDefinition(new InvocationRequestDefinition()
            .request(invocationApi.builder(DynamicRestCallerApi.class)
                .build(api -> api.callDynamicRest(null, null, null, null, null, null,
                    Invocations.mapOf(new HashMap<>(), Object.class))))
            .addResolversItem(new InvocationParameterResolver()
                .position(0)
                .name("serviceConnectionName")
                .definition(ObjectMappingDefinitionBuilder.create()
                    .constant(RESTFUL_CONNECTION_NAME).build()))
            .addResolversItem(new InvocationParameterResolver()
                .position(1)
                .name("path")
                .definition(ObjectMappingDefinitionBuilder.create()
                    .constant(TEST3_PATH).build()))
            .addResolversItem(new InvocationParameterResolver()
                .position(2)
                .name("httpMethod")
                .definition(ObjectMappingDefinitionBuilder.create()
                    .constant(TEST3_METHOD.toString()).build()))
            .addResolversItem(new InvocationParameterResolver()
                .position(3)
                .name("contentType")
                .definition(ObjectMappingDefinitionBuilder.create()
                    .constant(MediaType.APPLICATION_JSON_VALUE).build()))
            .addResolversItem(new InvocationParameterResolver()
                .position(4)
                .name("header")
                .definition(ObjectMappingDefinitionBuilder.create()
                    .constant(null).build()))
            .addResolversItem(new InvocationParameterResolver()
                .position(5)
                .name("body")
                .definition(ObjectMappingDefinitionBuilder
                    .create().constant(ObjectMappingDefinitionBuilder.create()
                        .addMapping(m -> m
                            .fromPath(List.of("name"))
                            .toPath(List.of("name")))
                        .addMapping(m -> m
                            .fromPath(List.of("year"))
                            .toPath(List.of("data", "year")))
                        .addMapping(m -> m
                            .fromPath(List.of("price"))
                            .toPath(List.of("data", "price")))
                        .addMapping(m -> m
                            .fromPath(List.of("model"))
                            .toPath(List.of("data", "CPU model")))
                        .addMapping(m -> m
                            .fromPath(List.of("diskSize"))
                            .toPath(List.of("data", "Hard disk size")))
                        .build())
                    .build()))
            .addResolversItem(new InvocationParameterResolver()
                .position(6)
                .name("params")
                .definition(ObjectMappingDefinitionBuilder.create().addMapping(m -> m
                    .expression("#allParams")).build())));

    methodTemplateEntry
        .save(objectApi.create("test",
            template));

    // "{\"name\": \"Apple MacBook Pro 16\", \"data\": {\"year\": 2019, \"price\": 1849.99, \"CPU
    // model\": \"Intel Core i9\", \"Hard disk size\": \"1 TB\"}}"


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
            MediaType.APPLICATION_JSON_VALUE,
            null, bodyDef, TEST3_PARAMS);
    assertNotNull(responseEntity);
    assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    Object body = responseEntity.getBody();
  }

  @Test
  void testMethodTemplate() throws ApiNotFoundException {
    InvocationRequest request = new InvocationRequest().interfaceClass("kiscica").name("kiscica")
        .methodName("teszt")
        .addParametersItem(new InvocationParameter().name("name").value("Apple MacBook Noob 16"))
        .addParametersItem(new InvocationParameter().name("year").value("2000"))
        .addParametersItem(new InvocationParameter().name("price").value("20 milpengő"))
        .addParametersItem(new InvocationParameter().name("model").value("CpuModel comes here"))
        .addParametersItem(new InvocationParameter().name("diskSize").value("Végtelen TB"));

    InvocationParameter result = invocationApi.invoke(request);
    ResponseEntity<Object> responseEntity = (ResponseEntity) result.getValue();
    assertNotNull(responseEntity);
    assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
  }
}
