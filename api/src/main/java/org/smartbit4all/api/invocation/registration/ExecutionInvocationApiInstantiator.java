package org.smartbit4all.api.invocation.registration;

import org.smartbit4all.api.apiregister.bean.ApiInfo;
import org.smartbit4all.api.invocation.ApiInvocationHandler;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.Invocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * With the {@value this#getProtocol()} protokol there is a proxy created without any
 * implementation. This way the call is delegated to specified execution api which is (in most
 * cases) a remote one.
 */
public class ExecutionInvocationApiInstantiator implements ProtocolSpecificApiInstantiator {

  public static final String APIINFO_PARAM_EXECUTION_API = "executionApi";

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private ApplicationContext appContext;

  @Override
  public Object instantiate(ApiInfo apiInfo) throws Exception {
    String interfaceQualifiedName = apiInfo.getInterfaceQualifiedName();
    String executionApi = apiInfo.getParameters().get(APIINFO_PARAM_EXECUTION_API);
    Class<?> interfaceType = Class.forName(interfaceQualifiedName);
    InvocationExecutionDelegation delegationAsOriginalApi =
        new InvocationExecutionDelegation(invocationApi, executionApi, appContext);
    return ApiInvocationHandler.createProxy(interfaceType, delegationAsOriginalApi, invocationApi,
        executionApi);
  }

  @Override
  public String getProtocol() {
    return Invocations.EXECUTION;
  }

  public static class InvocationExecutionDelegation implements InvocationExecutionApi {

    InvocationApi invocationApi;
    String executionApi;
    ApplicationContext appContext;

    public InvocationExecutionDelegation(InvocationApi invocationApi, String executionApi,
        ApplicationContext appContext) {
      this.invocationApi = invocationApi;
      this.executionApi = executionApi;
      this.appContext = appContext;
    }

    @Override
    public String getName() {
      return "invocation-execution-delegation";
    }

    @Override
    public InvocationParameter invoke(InvocationRequest request) throws Exception {
      Object apiInstance = appContext.getBean(request.getApiClass());
      InvocationRequest modifiedRequest =
          Invocations.getModifiedRequestToCallInnerApi(request, apiInstance, executionApi);
      return invocationApi.invoke(modifiedRequest);
    }

  }

}
