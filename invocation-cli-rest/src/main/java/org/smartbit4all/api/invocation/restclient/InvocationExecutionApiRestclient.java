package org.smartbit4all.api.invocation.restclient;

import java.net.URI;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
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
  private ObjectMapper objectMapper;

  @Override
  public InvocationParameter invoke(ServiceConnection serviceConnection,
      InvocationRequest request) {

    // TODO url összeállítása
    String url = serviceConnection.getEndpoint();
    HttpHeaders headers = new HttpHeaders();
    String sessionToken = serviceConnection.getAuthToken();
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

}
