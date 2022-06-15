package org.smartbit4all.api.invocation.restserver.impl;

import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.restserver.InvocationApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvocationApiDelegateImpl implements InvocationApiDelegate {

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public ResponseEntity<InvocationParameter> invokeApi(
      InvocationRequest body) throws Exception {

    for (InvocationParameter param : body.getParameters()) {
      Invocations.resolveParam(objectMapper, param);
    }

    InvocationParameter result = invocationApi.invoke(body);

    return ResponseEntity.ok(result);
  }

}
