package org.smartbit4all.api.invocation.restclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerPort;
import org.mockserver.springtest.MockServerTest;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationParameter.Kind;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.model.InvocationParameterData;
import org.smartbit4all.api.invocation.model.InvocationParameterKind;
import org.smartbit4all.api.invocation.registration.ApiInfo;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.restclient.apiregister.RestclientFactoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@MockServerTest()
@SpringBootTest(
    classes = {
        InvocationExecutionApiRestclientTestConfig.class
    },
    properties = {
    })
public class InvocationExecutionApiRestclientTests {

  private MockServerClient mockServerClient;
  
  @MockServerPort
  Integer mockServerPort;
  
  
  @Autowired
  private InvocationApi invocationApi;
  
  @Autowired
  private ApiRegister apiRegister;
  

  @Test
  public void testRestExecutionCall() throws Exception {
    ApiInfo apiInfo = new ApiInfo();
    apiInfo.setApiIdentifier("invocationExecutionRest");
    apiInfo.setInterfaceQualifiedName(InvocationExecutionApi.class.getName());
    apiInfo.setProtocol(Invocations.REST_GEN);
    apiInfo.addParameter(RestclientFactoryUtil.REST_BASE_PATH, "http://localhost:" + mockServerPort);
    apiRegister.register(apiInfo);
    
    String returnTypeClass = "java.lang.String";
    String returnKind = Kind.BYVALUE.name();
    String returnValue = "thisIsTheReturnValue";
    
    InvocationParameterData mockResponseData = new InvocationParameterData()
        .kind(InvocationParameterKind.BYVALUE)
        .typeClass(returnTypeClass)
        .value(returnValue);
    
    String mockResponse = new ObjectMapper().writeValueAsString(mockResponseData);
    
    mockServerClient
      .when(
          request()
          .withMethod("POST")
          )
      .respond(response()
          .withStatusCode(200)
          .withContentType(MediaType.APPLICATION_JSON)
          .withBody(mockResponse));
    
    
    InvocationRequest request = new InvocationRequest("org.smartbit4all.test.remote.TestApi1")
        .exec("invocationExecutionRest")
        .method("testMethod")
        .addParameter("param1", "this-is-a-string-param-at-param1")
        .addParameter("param2", Long.valueOf(222l));
    
    InvocationParameter returnParam = invocationApi.invoke(request);
    
    assertNotNull(returnParam);
    assertEquals(returnTypeClass, returnParam.getTypeClass());
    assertEquals(returnKind, returnParam.getKind().name());
    assertEquals(returnValue, returnParam.getStringValue());
    assertEquals(returnValue, returnParam.getValue());
    
  }

  
}
