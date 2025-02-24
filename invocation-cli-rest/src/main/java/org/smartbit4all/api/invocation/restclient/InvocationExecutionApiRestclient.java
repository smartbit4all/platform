package org.smartbit4all.api.invocation.restclient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApiImpl;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationError;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The generic rest api call for other server.
 * 
 * @author Peter Boros
 */
public class InvocationExecutionApiRestclient implements InvocationExecutionApi {

  private static final Logger log = LoggerFactory.getLogger(InvocationExecutionApiRestclient.class);

  // This key is used to get the apikey header key from the ServiceConnection.parameters map
  public static final String API_KEY_HEADER_KEY = "apiKeyHeader";

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired(required = false)
  private RestInvocationRequestCustApi customizer;

  @Override
  public InvocationParameter invoke(ServiceConnection serviceConnection,
      InvocationRequest request) throws ApiNotFoundException {

    String url = serviceConnection.getEndpoint();

    // Collect the BinaryData input parameters. If there is any then we have to call some upload.
    List<BinaryData> binaryDataInputList = getAndSetupBinaryDataParameters(request);

    // We decide which endpoint to use depending on the proposed return value of the request.
    boolean binaryUpload = !binaryDataInputList.isEmpty();
    boolean binaryResult = BinaryData.class.getName().equals(request.getReturnTypeClass());
    url = getProperURL(url, binaryUpload, binaryResult);

    HttpHeaders headers = new HttpHeaders();
    String authToken = serviceConnection.getAuthToken();
    Object headerKeyObj = serviceConnection.getParameters().get(API_KEY_HEADER_KEY);
    if (!ObjectUtils.isEmpty(authToken) && !ObjectUtils.isEmpty(headerKeyObj)) {
      headers.add(headerKeyObj.toString(), authToken);
    }

    String username = serviceConnection.getUsername();
    String password = serviceConnection.getPassword();
    if (!ObjectUtils.isEmpty(serviceConnection.getUsername())) {
      String str = username + ":" + (password == null ? "" : password);
      headers.add(HttpHeaders.AUTHORIZATION,
          "Basic " + Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8)));
    }

    if (customizer != null) {
      customizer.customizeRequest(request, headers);
    }

    final BodyBuilder requestBuilder = RequestEntity
        .method(HttpMethod.POST, URI.create(url))
        .accept(binaryResult ? MediaType.APPLICATION_OCTET_STREAM : MediaType.APPLICATION_JSON)
        .contentType(binaryUpload ? MediaType.MULTIPART_FORM_DATA : MediaType.APPLICATION_JSON)
        .headers(headers);

    // If binary upload then append the multipart contents els the body is the request itself.
    RequestEntity<Object> requestEntity =
        requestBuilder.body(getBody(request, binaryDataInputList));


    InvocationParameter respParam = null;
    if (binaryResult) {
      ResponseEntity<Resource> resp = null;
      try {
        resp =
            restTemplate.exchange(requestEntity, Resource.class);
      } catch (HttpStatusCodeException ex) {
        throwProperException(request, ex);
      } catch (RestClientException e) {
        throw new IllegalArgumentException(e.getMessage(), e);
      }
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
      ResponseEntity<InvocationParameter> resp;
      try {
        resp = restTemplate.exchange(requestEntity, InvocationParameter.class);
        respParam = resp.getBody();
        Invocations.resolveParam(objectMapper, respParam);
      } catch (HttpStatusCodeException ex) {
        throwProperException(request, ex);
      } catch (RestClientException e) {
        throw new IllegalArgumentException(e.getMessage(), e);
      }
    }

    return respParam;
  }

  private void throwProperException(InvocationRequest request, HttpStatusCodeException ex)
      throws ApiNotFoundException {
    HttpStatusCode statusCode = ex.getStatusCode();
    // Handle specific cases
    if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
      IllegalArgumentException result = null;
      // Extract response body
      String responseBody = ex.getResponseBodyAsString();
      if (responseBody != null) {
        try {
          InvocationError error = objectMapper.readValue(responseBody, InvocationError.class);
          // Create a new instance using the default constructor
          result = new IllegalArgumentException(error.getMessage());
          result.setStackTrace(Invocations.stackTraceElementsFromString(error.getStackTrace()));

        } catch (Exception e) {
          log.error("Unable to read InvocationError object from the response body.", e);
        }
      }
      if (result != null) {
        throw result;
      }
      throw new IllegalArgumentException(ex.getMessage(), ex);
    } else if (statusCode == HttpStatus.NOT_FOUND) {
      throw new ApiNotFoundException(request);
    } else {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }

  private Object getBody(InvocationRequest request, List<BinaryData> binaryDataInputList) {
    if (binaryDataInputList.isEmpty()) {
      return request;
    } else {
      MultipartBodyBuilder mpBuilder = new MultipartBodyBuilder();
      mpBuilder.part("invocationRequest", Invocations.stringifyRequest(objectMapper, request));
      mpBuilder.part("contents",
          binaryDataInputList.stream().map(b -> new InputStreamResource(b.inputStream())));
      return mpBuilder.build();
    }
  }

  private final String getProperURL(String url, boolean binaryUpload, boolean binaryResult) {
    String operation = null;
    if (binaryResult && binaryUpload) {
      operation = InvocationApiImpl.INVOKE_UPLOAD_DOWNLOAD;
    } else if (binaryUpload) {
      operation = InvocationApiImpl.INVOKE_UPLOAD;
    } else if (binaryResult) {
      operation = InvocationApiImpl.INVOKE_DOWNLOAD;
    }
    if (operation != null) {
      if (url.endsWith(InvocationApiImpl.INVOKE_API)) {
        url = url.replace(InvocationApiImpl.INVOKE_API, operation);
      } else {
        url += operation;
      }
    } else {
      if (!url.endsWith(InvocationApiImpl.INVOKE_API)) {
        url += InvocationApiImpl.INVOKE_API;
      }
    }
    return url;
  }

  /**
   * Collect the {@link BinaryData} input parameters and replace their value with their index in the
   * result list. Also set the class name for the server to ba able to look for the relevant content
   * from the list.
   * 
   * @param request
   * @return
   */
  private final List<BinaryData> getAndSetupBinaryDataParameters(InvocationRequest request) {
    List<BinaryData> result;
    if (request.getParameters() != null) {
      result = new ArrayList<>();
      for (InvocationParameter param : request.getParameters()) {
        if (param.getValue() instanceof BinaryData) {
          result.add((BinaryData) param.getValue());
          param.typeClass(BinaryData.class.getName()).value(result.size() - 1);
        }
      }
    } else {
      result = Collections.emptyList();
    }
    return result;
  }

}
