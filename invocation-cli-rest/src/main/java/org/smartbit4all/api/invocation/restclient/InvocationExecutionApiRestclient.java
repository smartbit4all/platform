package org.smartbit4all.api.invocation.restclient;

import java.io.IOException;
import java.net.URI;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.InvocationApiImpl;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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

    String url = serviceConnection.getEndpoint();
    // We decide which endpoint to use depending on the proposed return value of the request.
    boolean binaryResult = BinaryData.class.getName().equals(request.getReturnTypeClass());
    if (binaryResult) {
      if (url.endsWith(InvocationApiImpl.INVOKE_API)) {
        url = url.replace(InvocationApiImpl.INVOKE_API, InvocationApiImpl.INVOKE_DOWNLOAD);
      } else {
        url += InvocationApiImpl.INVOKE_DOWNLOAD;
      }
    } else {
      if (!url.endsWith(InvocationApiImpl.INVOKE_API)) {
        url += InvocationApiImpl.INVOKE_API;
      }
    }
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
    InvocationParameter respParam;
    if (binaryResult) {
      ResponseEntity<Resource> resp =
          restTemplate.exchange(requestEntity, Resource.class);
      if (resp.getBody() != null) {
        try {
          respParam = new InvocationParameter().typeClass(BinaryData.class.getName())
              .value(BinaryData.of(resp.getBody().getInputStream()));
        } catch (IOException e) {
          throw new IllegalArgumentException("Unable to read the invocation result.", e);
        }
      } else {
        respParam = new InvocationParameter().typeClass(BinaryData.class.getName()).value(null);
      }
    } else {
      ResponseEntity<InvocationParameter> resp =
          restTemplate.exchange(requestEntity, InvocationParameter.class);
      respParam = resp.getBody();
      Invocations.resolveParam(objectMapper, respParam);
    }

    return respParam;
  }

}
