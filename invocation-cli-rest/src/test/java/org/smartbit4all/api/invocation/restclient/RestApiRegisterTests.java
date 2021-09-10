package org.smartbit4all.api.invocation.restclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerPort;
import org.mockserver.springtest.MockServerTest;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationParameter.Kind;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.registration.ApiInfo;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.restclient.apiregister.RestclientFactoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@MockServerTest()
@SpringBootTest(
    classes = {
        PlatformApiConfig.class,
        RestApiRegisterTestConfig.class,
        InvocationRestClientConfig.class
    },
    properties = {
    })
public class RestApiRegisterTests {

  private MockServerClient mockServerClient;
  
  @MockServerPort
  Integer mockServerPort;
  
  @Autowired
  private ApiRegister apiRegister;
  
  @Autowired
  private InvocationApi invExcApi;
  
  @Test
  public void testRegistration() throws Exception {
    
    
    String returnTypeClass = "java.lang.String";
    String returnKind = Kind.BYVALUE.name();
    String returnValue = "thisIsTheReturnValue";
    
    mockServerClient
      .when(
          request()
          .withMethod("POST"), Times.unlimited())
      .respond(response()
          .withStatusCode(200)
          .withContentType(MediaType.APPLICATION_JSON)
          .withBody(returnValue));
    
    ApiInfo apiInfo = new ApiInfo();
    apiInfo.setApiIdentifier("testRestclient");
    apiInfo.setInterfaceQualifiedName(TestRestclient.class.getName());
    apiInfo.setProtocol(Invocations.REST_GEN);
    apiInfo.addParameter(RestclientFactoryUtil.REST_BASE_PATH, "http://localhost:" + mockServerPort);
    apiRegister.register(apiInfo);
    
    InvocationRequest request = new InvocationRequest(TestRestclient.class)
        .exec("rest-gen")
        .method("doSomething")
        .addParameter("param1", "myParameter");
    InvocationParameter returnParam = invExcApi.invoke(request);
    
    assertNotNull(returnParam);
    assertEquals(returnTypeClass, returnParam.getTypeClass());
    assertEquals(returnKind, returnParam.getKind().name());
    assertEquals(returnValue, returnParam.getStringValue());
    assertEquals(returnValue, returnParam.getValue());
    
  }

  
}
