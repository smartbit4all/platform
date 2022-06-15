package org.smartbit4all.api.invocation.restclient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.smartbit4all.api.invocation.restclientgen.util.ApiClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

public class TestRestclientImpl implements TestRestclient {

  private ApiClient apiClient;

  public TestRestclientImpl(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  @Override
  public String doSomething(String param) {

    Object postBody = param;

    // verify the required parameter 'orgSmartbit4allApiInvocationModelInvocationRequestData' is set
    if (param == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
          "Missing the required parameter 'param' when calling invokeApi");
    }

    String path = apiClient.expandPath("/doSomething", Collections.<String, Object>emptyMap());

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    final HttpHeaders headerParams = new HttpHeaders();
    final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
    final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();

    final String[] localVarAccepts = {
        "application/json"
    };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] contentTypes = {
        "application/json"
    };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(contentTypes);

    String[] authNames = new String[] {};

    ParameterizedTypeReference<String> returnType = new ParameterizedTypeReference<String>() {};
    return apiClient
        .invokeAPI(path, HttpMethod.POST, new HashMap<>(), queryParams, postBody, headerParams,
            cookieParams, formParams, localVarAccept, localVarContentType, authNames, returnType)
        .getBody();
  }


}
