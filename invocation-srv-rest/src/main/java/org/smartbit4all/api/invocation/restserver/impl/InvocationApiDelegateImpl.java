package org.smartbit4all.api.invocation.restserver.impl;

import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.model.InvocationParameterData;
import org.smartbit4all.api.invocation.model.InvocationRequestData;
import org.smartbit4all.api.invocation.restserver.InvocationApiDelegate;
import org.smartbit4all.api.invocation.restserver.InvocationRestSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class InvocationApiDelegateImpl implements InvocationApiDelegate {

  @Autowired
  private InvocationApi invocationApi;

  @Override
  public ResponseEntity<InvocationParameterData> invokeApi(
      InvocationRequestData invocationRequestData)
      throws Exception {

    InvocationRequest invocationRequest =
        InvocationRestSerializer.invocationRequestData2Request(invocationRequestData);

    InvocationParameter result = invocationApi.invoke(invocationRequest);

    InvocationParameterData resultData =
        InvocationRestSerializer.invocationParameter2ParameterData(result);

    return ResponseEntity.ok(resultData);
  }

}
