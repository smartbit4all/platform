package org.smartbit4all.api.invocation.restclient;

import java.util.UUID;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The generic rest api call for other server.
 * 
 * @author Peter Boros
 */
public class InvocationExecutionApiRestclient implements InvocationExecutionApi {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ApplicationRuntimeApi applicationRuntimeApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public InvocationParameter invoke(UUID runtime, InvocationRequest request) {

    ApplicationRuntime applicationRuntime = applicationRuntimeApi.get(runtime);
    String ipAddress = applicationRuntime.getIpAddress();
    int serverPort = applicationRuntime.getServerPort();

    // TODO url összeállítása
    String url = "http://" + ipAddress + ":" + serverPort;

    ResponseEntity<InvocationParameter> resp = restTemplate
        .postForEntity(url + "/invokeApi", request, InvocationParameter.class);

    InvocationParameter respParam = resp.getBody();

    Invocations.resolveParam(objectMapper, respParam);
    return respParam;
  }
}
