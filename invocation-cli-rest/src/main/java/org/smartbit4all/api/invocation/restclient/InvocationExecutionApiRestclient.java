package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.invocation.InvocationExecutionApiImpl;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.model.InvocationParameterData;
import org.smartbit4all.api.invocation.model.InvocationRequestData;
import org.smartbit4all.api.invocation.restclientgen.InvocationApi;
import org.smartbit4all.api.invocation.restclientgen.util.ApiClient;

/**
 * The generic rest api call for other server.
 * 
 * @author Peter Boros
 */
public class InvocationExecutionApiRestclient extends InvocationExecutionApiImpl {

  private InvocationApi invocationApi;

  private InvocationExecutionApiRestclient(String name, InvocationApi invocationApi) {
    super(name);
    this.invocationApi = invocationApi;
  }

  @Override
  public InvocationParameter invoke(InvocationRequest request) throws ClassNotFoundException {
    
    InvocationRequestData invocationRequestData =
        InvocationRestSerializer.invocationRequest2Data(request);
    
    InvocationParameterData result = invocationApi.invokeApi(invocationRequestData);
    
    return InvocationRestSerializer.invocationParameterData2Parameter(result);
  }

  /**
   * Creates an instance with a preset rest {@link InvocationApi}.</br>
   * This factory method can be useful when instantiating the class during configuration time.  
   */
  public static InvocationExecutionApiRestclient create(String name, InvocationApi invocationApi) {
    return new InvocationExecutionApiRestclient(name, invocationApi);
  }
  
  public static InvocationExecutionApiRestclient create(String apiIdentifier,
      ApiClient apiClient) {
    InvocationApi restApi = new InvocationApi(apiClient);
    return new InvocationExecutionApiRestclient(apiIdentifier, restApi);
  }
  
  
}
