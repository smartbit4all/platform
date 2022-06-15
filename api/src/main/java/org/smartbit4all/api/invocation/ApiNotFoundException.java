package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

public class ApiNotFoundException extends Exception {

  public ApiNotFoundException(InvocationRequest request) {
    super("No API found for request! " + request);
  }

  public ApiNotFoundException(ApiData apiData) {
    super("No active runtime found that provides the API! " + apiData);
  }

}
