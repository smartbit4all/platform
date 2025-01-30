package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

/**
 * The execution api for the invocations. It is a generic caller of services that accept all the
 * paremeters for the call and execute the call itself.
 * 
 * @author Peter Boros
 */
public interface InvocationExecutionApi {

  /**
   * Executes the invocation request over the implemented protocol.
   * 
   * @param serviceConnection The service connection parameter to access the api endpoint.
   * @param request
   * @return
   */
  InvocationParameter invoke(ServiceConnection serviceConnection, InvocationRequest request)
      throws ApiNotFoundException;

}
