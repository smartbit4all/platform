package org.smartbit4all.api.navigation.restclientgen;

import org.smartbit4all.api.navigation.service.util.ApiClient;

import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationResponse;
import java.net.URI;

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
@Component("org.smartbit4all.api.navigation.restclientgen.NavigationApi")
public class NavigationApi {
    private ApiClient apiClient;

    public NavigationApi() {
        this(new ApiClient());
    }

    @Autowired
    public NavigationApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Retrieve the entries from the navigations.
     * 
     * <p><b>200</b> - The navigation entry if we found it or null if missing
     * @param entryMetaUri  (required)
     * @param objectUri  (required)
     * @return NavigationEntry
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) throws RestClientException {
        return getEntryWithHttpInfo(entryMetaUri, objectUri).getBody();
    }

    /**
     * Retrieve the entries from the navigations.
     * 
     * <p><b>200</b> - The navigation entry if we found it or null if missing
     * @param entryMetaUri  (required)
     * @param objectUri  (required)
     * @return ResponseEntity&lt;NavigationEntry&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<NavigationEntry> getEntryWithHttpInfo(URI entryMetaUri, URI objectUri) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'entryMetaUri' is set
        if (entryMetaUri == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'entryMetaUri' when calling getEntry");
        }
        
        // verify the required parameter 'objectUri' is set
        if (objectUri == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'objectUri' when calling getEntry");
        }
        

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (entryMetaUri != null)
            formParams.add("entryMetaUri", entryMetaUri);
        if (objectUri != null)
            formParams.add("objectUri", objectUri);

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] contentTypes = { 
            "application/x-www-form-urlencoded"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<NavigationEntry> returnType = new ParameterizedTypeReference<NavigationEntry>() {};
        return apiClient.invokeAPI("/entry", HttpMethod.POST, Collections.<String, Object>emptyMap(), queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, authNames, returnType);
    }
    /**
     * Queries all data sources to populate the associations starting from the given entry.
     * 
     * <p><b>200</b> - The map of the references by the URI of association meta we passed in the associations parameter.
     * @param objectUri The URI of the api object that is the starting point of the navigation. It must be a valid URI that can be the starting point of the associations we provided. (required)
     * @param associationMetaUris The list of associations to identify the direction we want to navigate. If we skip this parameter (null) then we will have all the associations defined in the meta (optional)
     * @return List&lt;NavigationResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<NavigationResponse> navigate(URI objectUri, List<URI> associationMetaUris) throws RestClientException {
        return navigateWithHttpInfo(objectUri, associationMetaUris).getBody();
    }

    /**
     * Queries all data sources to populate the associations starting from the given entry.
     * 
     * <p><b>200</b> - The map of the references by the URI of association meta we passed in the associations parameter.
     * @param objectUri The URI of the api object that is the starting point of the navigation. It must be a valid URI that can be the starting point of the associations we provided. (required)
     * @param associationMetaUris The list of associations to identify the direction we want to navigate. If we skip this parameter (null) then we will have all the associations defined in the meta (optional)
     * @return ResponseEntity&lt;List&lt;NavigationResponse&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<NavigationResponse>> navigateWithHttpInfo(URI objectUri, List<URI> associationMetaUris) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'objectUri' is set
        if (objectUri == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'objectUri' when calling navigate");
        }
        

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (objectUri != null)
            formParams.add("objectUri", objectUri);
        if (associationMetaUris != null)
            formParams.addAll("associationMetaUris", associationMetaUris);

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] contentTypes = { 
            "application/x-www-form-urlencoded"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<NavigationResponse>> returnType = new ParameterizedTypeReference<List<NavigationResponse>>() {};
        return apiClient.invokeAPI("/navigate", HttpMethod.POST, Collections.<String, Object>emptyMap(), queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, authNames, returnType);
    }
}
