package org.smartbit4all.api.apiregister.restclientgen;

import org.smartbit4all.api.apiregister.restclientgen.util.ApiClient;


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
@Component("org.smartbit4all.api.apiregister.restclientgen.ApiRegisterApi")
public class ApiRegisterApi {
    private ApiClient apiClient;

    public ApiRegisterApi() {
        this(new ApiClient());
    }

    @Autowired
    public ApiRegisterApi(ApiClient apiClient) {
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
     * @param body  (required)
     * @return org.smartbit4all.api.apiregister.bean.Registration
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public org.smartbit4all.api.apiregister.bean.Registration register(org.smartbit4all.api.apiregister.bean.ApiInfo body) throws RestClientException {
        return registerWithHttpInfo(body).getBody();
    }

    /**
     * 
     * 
     * <p><b>200</b>
     * @param body  (required)
     * @return ResponseEntity&lt;org.smartbit4all.api.apiregister.bean.Registration&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<org.smartbit4all.api.apiregister.bean.Registration> registerWithHttpInfo(org.smartbit4all.api.apiregister.bean.ApiInfo body) throws RestClientException {
        Object postBody = body;
        
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling register");
        }
        
        String path = apiClient.expandPath("/register", Collections.<String, Object>emptyMap());

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

        ParameterizedTypeReference<org.smartbit4all.api.apiregister.bean.Registration> returnType = new ParameterizedTypeReference<org.smartbit4all.api.apiregister.bean.Registration>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, authNames, returnType);
    }
    /**
     * 
     * 
     * <p><b>200</b>
     * @param body  (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void unregister(org.smartbit4all.api.apiregister.bean.Registration body) throws RestClientException {
        unregisterWithHttpInfo(body);
    }

    /**
     * 
     * 
     * <p><b>200</b>
     * @param body  (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> unregisterWithHttpInfo(org.smartbit4all.api.apiregister.bean.Registration body) throws RestClientException {
        Object postBody = body;
        
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling unregister");
        }
        
        String path = apiClient.expandPath("/unregister", Collections.<String, Object>emptyMap());

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] contentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, authNames, returnType);
    }
}
