package org.smartbit4all.api.invocation.restclient;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.collection.bean.ObjectLookupResult;
import org.smartbit4all.api.invocation.bean.ResponseEntityObject;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.core.object.ContextObject;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.util.UriComponentsBuilder;
import com.google.common.cache.Cache;

public class DynamicRestCallerApiImpl implements DynamicRestCallerApi {

  private static final Logger log = LoggerFactory.getLogger(DynamicRestCallerApiImpl.class);

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private Cache<String, ResponseEntity<Object>> dynamicRestRequestCache;

  @Override
  public ResponseEntityObject callDynamicRest(String serviceConnectionName,
      String path,
      String httpMethodString,
      String contentTypeName,
      ObjectMappingDefinition header,
      ObjectMappingDefinition queryParams,
      ObjectMappingDefinition body,
      Map<String, Object> params) {
    return callDynamicRest(serviceConnectionName, path, httpMethodString, contentTypeName, header,
        queryParams, body, null, params);
  }

  @Override
  public ResponseEntityObject callDynamicRestWithContent(String serviceConnectionName, String path,
      String httpMethodString, String contentTypeName, ObjectMappingDefinition header,
      ObjectMappingDefinition queryParams, BinaryContentData binaryContentData,
      Map<String, Object> params) {
    return callDynamicRest(serviceConnectionName, path, httpMethodString, contentTypeName, header,
        queryParams, null, binaryContentData, params);
  }

  private ResponseEntityObject callDynamicRest(String serviceConnectionName,
      String path,
      String httpMethodString,
      String contentTypeName,
      ObjectMappingDefinition header,
      ObjectMappingDefinition queryParams,
      ObjectMappingDefinition body,
      BinaryContentData binaryContentData,
      Map<String, Object> params) {
    Objects.requireNonNull(serviceConnectionName,
        "ServiceConnection name can not be null");
    ServiceConnection serviceConnection = getServiceConnection(serviceConnectionName);
    if (serviceConnection == null) {
      throw new IllegalArgumentException(
          "Could not find service connection with name: " + serviceConnectionName);
    }
    HttpMethod httpMethod = HttpMethod.valueOf(httpMethodString);
    MediaType contentType = contentTypeName != null ? MediaType.valueOf(contentTypeName) : null;
    Map<String, Object> serviceConnectionParameters = serviceConnection.getParameters();
    ContextObject contextObject = objectApi.contextObject().set(params);
    contextObject.set("serviceConnection", serviceConnection);
    Object bodyObj = constructBodyObj(body, binaryContentData, contextObject, contentType);
    Map<String, String> headerMap = constructHeaderMap(header, contextObject);
    Map<String, String> queryParamsMap = constructQueryParamsMap(queryParams, contextObject);

    RestClient restClient = RestClientHelper.getRestClient(serviceConnection);

    // TODO HttpMethod parameter, contentType? uri endpoint or endpoint + path param. Headers?
    /*
     * maybe bean of call params: String serviceConnectionName, String path, HttpMethod httpMethod
     * MediaType contentType,
     */
    String endpoint = serviceConnection.getEndpoint();
    String baseUri = path == null ? endpoint : endpoint + path;

    // add query params
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUri);
    queryParamsMap.forEach(builder::queryParam);

    URI uri = builder.build().encode().toUri();
    RequestBodySpec request = restClient.method(httpMethod)
        .uri(uri)
        .body(bodyObj)
        .contentType(contentType)
        .headers(h -> headerMap.forEach((key, value) -> h.add(key, value)));

    ResponseEntity<Object> response;
    try {
      response = dynamicRestRequestCache.get(uri.toString(), () -> request
          .retrieve()
          .toEntity(Object.class));
    } catch (ExecutionException e) {
      response = request
          .retrieve()
          .toEntity(Object.class);
    }

    ResponseEntityObject responseEntityObject = new ResponseEntityObject();

    if (response.getHeaders() != null) {
      responseEntityObject.headers(objectApi.toMapObject(response.getHeaders()));
    }
    return responseEntityObject.body(response.getBody())
        .statusCode(response.getStatusCode().toString())
        .statusCodeValue(response.getStatusCode().value());
  }

  private Map<String, String> constructHeaderMap(ObjectMappingDefinition header,
      ContextObject contextObject) {
    Object headerObj = null;
    if (header != null) {
      headerObj = objectApi.mapper().setContext(contextObject).mapping(header).execute();
    }
    Map<String, String> headerMap = objectApi.asMap(String.class, objectApi.toMapObject(headerObj));
    return headerMap;
  }

  private Object constructBodyObj(ObjectMappingDefinition body, BinaryContentData binaryContentData,
      ContextObject contextObject,
      MediaType contentType) {
    Object bodyObj = "";
    if (MediaType.APPLICATION_OCTET_STREAM.equals(contentType) && binaryContentData != null) {
      BinaryDataObject binaryDataObject =
          objectApi.loadLatest(binaryContentData.getDataUri()).getObject(BinaryDataObject.class);
      bodyObj = new InputStreamResource(
          binaryDataObject.getBinaryData().inputStream());
    } else if (body != null) {
      bodyObj = objectApi.mapper().setContext(contextObject).mapping(body).execute();
    }
    return bodyObj;
  }

  private Map<String, String> constructQueryParamsMap(ObjectMappingDefinition queryParams,
      ContextObject contextObject) {
    if (queryParams != null) {
      Object queryParamsObj =
          objectApi.mapper().setContext(contextObject).mapping(queryParams).execute();
      return objectApi.asMap(String.class, objectApi.toMapObject(queryParamsObj));
    }
    return Collections.emptyMap();
  }

  private ServiceConnection getServiceConnection(String serviceConnectionName) {
    MDMEntryApi entryApi = masterDataManagementApi.getApi(
        MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        InvocationApiMdmConfig.MDM_ENTRY_SERVICECONNECTION);
    ObjectLookupResult lookupResult =
        entryApi.lookup().lookup(Map.of(ServiceConnection.NAME, serviceConnectionName));
    if (lookupResult == null || lookupResult.getItems() == null
        || lookupResult.getItems().isEmpty()) {
      return null;
    }
    Map<String, Object> objectAsMap = lookupResult.getItems().get(0).getObjectAsMap();
    return objectApi.asType(ServiceConnection.class,
        objectAsMap);
  }

}
