package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.springframework.http.HttpHeaders;

/**
 * Thei API can be implemented at project level to customize the
 * {@link InvocationExecutionApiRestclient}.
 */
public interface RestInvocationRequestCustApi {


  /**
   * This function can be used to add or remove http headers before rest call, or changer the
   * {@InvocationRequest}.
   * 
   * @param request
   * @param header
   */

  void customizeRequest(InvocationRequest request, HttpHeaders header);

}
