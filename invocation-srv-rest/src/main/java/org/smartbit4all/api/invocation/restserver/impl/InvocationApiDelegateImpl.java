package org.smartbit4all.api.invocation.restserver.impl;

import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.restserver.InvocationApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

    InvocationParameter result = invokeBase(body);

    return ResponseEntity.ok(result);
  }

  private InvocationParameter invokeBase(InvocationRequest body) throws ApiNotFoundException {
    for (InvocationParameter param : body.getParameters()) {
      Invocations.resolveParam(objectMapper, param);
    }

    InvocationParameter result = invocationApi.invoke(body);
    return result;
  }

  @Override
  public ResponseEntity<Resource> invokeDownload(InvocationRequest body)
      throws Exception {
    InvocationParameter result = invokeBase(body);
    if (result.getValue() instanceof BinaryData) {
      return ResponseEntity
          .ok(new InputStreamResource(((BinaryData) result.getValue()).inputStream()));
    }
    return ResponseEntity.ok().body(null);
  }

}
