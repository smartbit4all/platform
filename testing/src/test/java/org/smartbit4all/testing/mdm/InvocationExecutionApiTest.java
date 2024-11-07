package org.smartbit4all.testing.mdm;

import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

/**
 * Created to simulate an invocation call. For test purposes it saves the last invocation request to
 * access it from the test case.
 */
public class InvocationExecutionApiTest implements InvocationExecutionApi {

  public static InvocationRequest lastRequest;

  @Override
  public InvocationParameter invoke(ServiceConnection serviceConnection,
      InvocationRequest request) {
    lastRequest = request;
    return null;
  }

}
