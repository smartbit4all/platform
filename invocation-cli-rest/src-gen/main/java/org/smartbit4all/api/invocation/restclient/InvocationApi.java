package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.invocation.restclient.util.ApiClient;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
@Component("org.smartbit4all.api.invocation.restclient.InvocationApi")
public class InvocationApi {
    private ApiClient apiClient;

    public InvocationApi() {
        this(new ApiClient());
    }

    @Autowired
    public InvocationApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * 
     * 
     * <p><b>200</b>
     * @param orgSmartbit4allApiInvocationModelInvocationRequestData  (required)
     * @return org.smartbit4all.api.invocation.model.InvocationParameterData
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public org.smartbit4all.api.invocation.model.InvocationParameterData invokeApi(org.smartbit4all.api.invocation.model.InvocationRequestData orgSmartbit4allApiInvocationModelInvocationRequestData) throws RestClientException {
        return invokeApiWithHttpInfo(orgSmartbit4allApiInvocationModelInvocationRequestData).getBody();
    }

    /**
     * 
     * 
     * <p><b>200</b>
     * @param orgSmartbit4allApiInvocationModelInvocationRequestData  (required)
     * @return ResponseEntity&lt;org.smartbit4all.api.invocation.model.InvocationParameterData&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<org.smartbit4all.api.invocation.model.InvocationParameterData> invokeApiWithHttpInfo(org.smartbit4all.api.invocation.model.InvocationRequestData orgSmartbit4allApiInvocationModelInvocationRequestData) throws RestClientException {
        Object postBody = orgSmartbit4allApiInvocationModelInvocationRequestData;
        
        // verify the required parameter 'orgSmartbit4allApiInvocationModelInvocationRequestData' is set
        if (orgSmartbit4allApiInvocationModelInvocationRequestData == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'orgSmartbit4allApiInvocationModelInvocationRequestData' when calling invokeApi");
        }
        
        String path = apiClient.expandPath("/invokeApi", Collections.<String, Object>emptyMap());

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] contentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<org.smartbit4all.api.invocation.model.InvocationParameterData> returnType = new ParameterizedTypeReference<org.smartbit4all.api.invocation.model.InvocationParameterData>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, authNames, returnType);
    }
}
