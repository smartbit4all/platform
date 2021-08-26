package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.invocation.InvocationExecutionApiImpl;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.model.InvocationParameterData;

/**
 * The generic rest api call for other server.
 * 
 * @author Peter Boros
 */
public class InvocationExecutionApiRestclient extends InvocationExecutionApiImpl {

  private InvocationApi invocationApi;

  public InvocationExecutionApiRestclient(String name, InvocationApi invocationApi) {
    super(name);
    this.invocationApi = invocationApi;
  }

  @Override
  public InvocationParameter invoke(InvocationRequest request) throws ClassNotFoundException {
    // TODO serialize!
    InvocationParameterData result = invocationApi.invokeApi(null);
    return null;
  }

}
