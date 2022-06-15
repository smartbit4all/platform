package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * The {@link InvocationApi} is a generic api to call other api function.
 * 
 * @author Peter Boros
 */
public interface InvocationApi {

  /**
   * The generic api call.
   * 
   * @param request
   * @throws ApiNotFoundException
   */
  InvocationParameter invoke(InvocationRequest request) throws ApiNotFoundException;

}
