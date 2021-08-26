package org.smartbit4all.api.invocation.restserver.impl;

import java.util.Optional;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.model.InvocationParameterData;
import org.smartbit4all.api.invocation.model.InvocationRequestData;
import org.smartbit4all.api.invocation.restserver.InvocationApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class InvocationApiDelegateImpl implements InvocationApiDelegate {

  @Autowired
  private InvocationApi invocationApi;

  @Override
  public ResponseEntity<InvocationParameterData> invokeApi(
      InvocationRequestData orgSmartbit4allApiInvocationBeanInvocationRequestData)
      throws Exception {

    InvocationRequest invocationRequest =
        new InvocationRequest(orgSmartbit4allApiInvocationBeanInvocationRequestData.getApiClass());

    InvocationParameter result = invocationApi.invoke(invocationRequest);

    // Serialize result.
    InvocationParameterData resultData =
        result == null ? null
            : new InvocationParameterData()
                .value(result.getValue() == null ? null : result.getValue().toString());
    return ResponseEntity.of(Optional.of(resultData));
  }

}
