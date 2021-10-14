package org.smartbit4all.api.invocation.restserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.apiregister.bean.ApiInfo;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.model.InvocationParameterData;
import org.smartbit4all.api.invocation.model.InvocationParameterKind;
import org.smartbit4all.api.invocation.model.InvocationRequestData;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.LocalApiInstantiator;
import org.smartbit4all.api.invocation.restserver.InvocationApiRestServerTestConfig.TestApiInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@SpringBootTest(
    classes = {
        InvocationApiRestServerTestConfig.class
    },
    properties = {
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InvocationApiRestServerTest {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TestApiInterface testApi; // this is registered as a placeholder in the test config!

  @LocalServerPort
  private int serverPort;

  @Value("${openapi.invocation.base-path:/}")
  private String path;

  @Test
  public void callApiRegisterThroughInvocationRestServer() throws Exception {

    String testApiImplIdentifier =
        LocalApiInstantiator.createLocalApiParameter(new TestApiInterface() {

          @Override
          public String doSomething(String param) {
            return "return-" + param;
          }

        });

    // this is only a test scenario, normally we can not register a local api with a remote call :)
    ApiInfo apiInfo = new ApiInfo()
        .apiIdentifier("testApi")
        .interfaceQualifiedName(TestApiInterface.class.getName())
        .protocol(Invocations.LOCAL)
        .putParametersItem(LocalApiInstantiator.LOCAL_API_IMPL, testApiImplIdentifier);

    String apiInfoString = objectMapper.writeValueAsString(apiInfo);

    InvocationParameterData apiInfoParam = new InvocationParameterData()
        .kind(InvocationParameterKind.BYVALUE)
        .typeClass(ApiInfo.class.getName())
        .value(apiInfoString);

    InvocationRequestData invocationRequestData = new InvocationRequestData()
        .uuid(UUID.randomUUID())
        .apiClass(ApiRegister.class.getName())
        .executionApi("local")
        .methodName("register")
        .addParametersItem(apiInfoParam);

    restTemplate.postForObject(getUrl(), invocationRequestData, Void.class);


    String returnValue = testApi.doSomething("cica");
    assertEquals("return-cica", returnValue);
  }

  /**
   * http://localhost:port/path/invokeApi
   */
  private String getUrl() {
    StringBuilder sb = new StringBuilder();
    sb.append("http://localhost:")
        .append(serverPort)
        .append("/");
    if (!ObjectUtils.isEmpty(path)) {
      sb.append(path);
      sb.append("/");
    }
    sb.append("invokeApi");
    return sb.toString();
  }

}
