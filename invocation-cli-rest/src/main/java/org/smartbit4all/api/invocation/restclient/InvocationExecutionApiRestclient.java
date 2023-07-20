package org.smartbit4all.api.invocation.restclient;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.SessionInfoData;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
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

  @Autowired
  private SessionApi sessionApi;

  @Override
  public InvocationParameter invoke(UUID runtime, InvocationRequest request) {

    ApplicationRuntime applicationRuntime = applicationRuntimeApi.get(runtime);
    String ipAddress = applicationRuntime.getIpAddress();
    int serverPort = applicationRuntime.getServerPort();

    // TODO url összeállítása
    String url = "http://" + ipAddress + ":" + serverPort + "/invokeApi";

    HttpHeaders headers = new HttpHeaders();
    String sessionToken = getSessionToken();
    if (!ObjectUtils.isEmpty(sessionToken)) {
      headers.add("Authorization", "Bearer " + sessionToken);
    }

    final BodyBuilder requestBuilder = RequestEntity
        .method(HttpMethod.POST, URI.create(url))
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(headers);

    RequestEntity<Object> requestEntity = requestBuilder.body(request);
    ResponseEntity<InvocationParameter> resp =
        restTemplate.exchange(requestEntity, InvocationParameter.class);

    InvocationParameter respParam = resp.getBody();

    Invocations.resolveParam(objectMapper, respParam);
    return respParam;
  }

  private String getSessionToken() {
    return sessionApi.getParameter(SessionInfoData.SID);
  }

}
