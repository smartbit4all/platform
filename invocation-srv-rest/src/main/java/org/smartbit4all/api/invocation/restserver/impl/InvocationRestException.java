package org.smartbit4all.api.invocation.restserver.impl;

import org.smartbit4all.api.invocation.bean.InvocationRequest;

public class InvocationRestException extends Exception {

  InvocationRequest request;

  Exception ex;

  public InvocationRestException(InvocationRequest request, Exception ex) {
    super();
    this.request = request;
    this.ex = ex;
  }

}
