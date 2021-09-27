package org.smartbit4all.api.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationParameter.Kind;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.TestContributionApi;
import org.smartbit4all.api.invocation.TestPrimaryApi;
import org.smartbit4all.api.invocation.registration.ApiInfo;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.ExecutionInvocationApiInstantiator;
import org.smartbit4all.api.invocation.registration.LocalApiInstantiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = {
    ApiRegistrationTestConfig.class,
    CustomExecutionApiRegistrationTest.Config.class
})
public class CustomExecutionApiRegistrationTest {

  @Autowired
  private ApiRegister apiRegister;

  @Autowired
  private TestPrimaryApi primaryApi;

  @Autowired
  private InvocationApi invocationApi;

  private static final String configTimeExecutionApi = "configTimeExecutionApi";
  private static final String runtimeTimeExecutionApi = "runtimeExecutionApi";

  @Test
  public void apiRegistrationToCustomExecutionInvocation() throws Exception {

    /*
     * Registering a new TestContributionApi that we want to use by the TestPrimarayApi instance. We
     * specify this new TestContributionApi as it can be called through an existing (configured /
     * pre-registered) execustionApi. Since we know this existing api by name, we set the protocol
     * to execution-invocation and set this name as parameter.
     * 
     * This way, doing a call on the registered TestContributionApi will end in an invoke of the
     * specified executionApi.
     * 
     * This time in the test, the specified ExecutionApi is added in config time and the invoke
     * method is mocked to give the proper responses. In production these calls can be delegated as
     * other distributed application calls (i.e. rest calls).
     * 
     */

    ApiInfo apiInfo = new ApiInfo();
    String apiIdentifier = "testContributionApi-" + configTimeExecutionApi;
    apiInfo.setApiIdentifier(apiIdentifier);
    apiInfo.setInterfaceQualifiedName(TestContributionApi.class.getName());
    apiInfo.setProtocol(Invocations.EXECUTION);
    apiInfo.addParameter(ExecutionInvocationApiInstantiator.APIINFO_PARAM_EXECUTION_API,
        configTimeExecutionApi);

    apiRegister.register(apiInfo);

    TestContributionApi registeredContributionApi = primaryApi.findApiByName(apiIdentifier);

    assertNotNull(registeredContributionApi);
    assertEquals(apiIdentifier, registeredContributionApi.getApiName());

    InvocationRequest request = Invocations.invoke(TestContributionApi.class)
        .exec(configTimeExecutionApi)
        .method("whatever")
        .addParameter("p1", "param1");
    InvocationParameter retVal = invocationApi.invoke(request);

    assertEquals("TheResult", retVal.getValue());

    InvocationRequest request2 = Invocations.invoke(TestPrimaryApi.class)
        .innerApi(apiIdentifier)
        .method("whatever")
        .addParameter("p1", "param1");
    InvocationParameter retVal2 = invocationApi.invoke(request2);

    assertEquals("TheResult", retVal2.getValue());

  }

  @Test
  public void registerACustomExecutionInvocationApi() throws Exception {

    // first register the runtime execution api
    ApiInfo apiInfo = new ApiInfo();
    apiInfo.setApiIdentifier(runtimeTimeExecutionApi);
    apiInfo.setInterfaceQualifiedName(InvocationExecutionApi.class.getName());
    apiInfo.setProtocol(Invocations.LOCAL);
    apiInfo.addParameter(LocalApiInstantiator.LOCAL_API_IMPL,
        LocalApiInstantiator
            .createLocalApiParameter(createCustomExecutionApi(runtimeTimeExecutionApi)));

    apiRegister.register(apiInfo);

    // then register the contribution api that will use the runtime execution api
    apiInfo = new ApiInfo();
    String apiIdentifier = "testContributionApi-" + runtimeTimeExecutionApi;
    apiInfo.setApiIdentifier(apiIdentifier);
    apiInfo.setInterfaceQualifiedName(TestContributionApi.class.getName());
    apiInfo.setProtocol(Invocations.EXECUTION);
    apiInfo.addParameter(ExecutionInvocationApiInstantiator.APIINFO_PARAM_EXECUTION_API,
        runtimeTimeExecutionApi);

    apiRegister.register(apiInfo);

    // call the registered contribution api through the primary api
    InvocationRequest request2 = Invocations.invoke(TestPrimaryApi.class)
        .innerApi(apiIdentifier)
        .method("whatever")
        .addParameter("p1", "param1");
    InvocationParameter retVal2 = invocationApi.invoke(request2);

    assertEquals("TheResult", retVal2.getValue());

  }


  private static InvocationExecutionApi createCustomExecutionApi(String apiName) {
    return new InvocationExecutionApi() {

      @Override
      public String getName() {
        return apiName;
      }

      @Override
      public InvocationParameter invoke(InvocationRequest request) throws Exception {
        assertNotNull(request);
        /*
         * here we don't care about the request. The invoke calls should be delegated here by the
         * value of the InvocationRequest.executionApi field. We simply mock the responses to the
         * required method calls, not handling the request's other parameters.
         */

        String result = "";
        if ("getApiName".equals(request.getMethodName())) {
          result = "testContributionApi-" + apiName;
        }
        if ("whatever".equals(request.getMethodName())) {
          List<InvocationParameter> parameters = request.getParameters();
          if (parameters == null || parameters.isEmpty()) {
            fail("the mock method can not be calles without a parameter!");
          }
          assertEquals("param1", parameters.get(0).getValue());
          result = "TheResult";
        }
        InvocationParameter retParam = new InvocationParameter();
        retParam.setKind(Kind.BYVALUE);
        retParam.setValue(result);
        return retParam;
      }

    };
  }


  @Configuration
  public static class Config {

    @Bean
    public ExecutionInvocationApiInstantiator executionInvocationApiInstantiator() {
      return new ExecutionInvocationApiInstantiator();
    }

    @Bean
    public InvocationExecutionApi configTimeExecutionApi() {
      return createCustomExecutionApi(configTimeExecutionApi);
    }

  }

}
