package org.smartbit4all.api.invocation.restclient;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.core.object.ContextObject;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;

public class DynamicRestCallerApiImpl implements DynamicRestCallerApi {

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Override
  public ResponseEntity<Object> callDynamicRest(String serviceConnectionName,
      String path,
      HttpMethod httpMethod,
      MediaType contentType,
      ObjectMappingDefinition header,
      ObjectMappingDefinition body,
      Map<String, Object> params) {
    Objects.requireNonNull(serviceConnectionName,
        "ServiceConnection name can not be null");
    ServiceConnection serviceConnection = getServiceConnection(serviceConnectionName);
    Objects.requireNonNull(serviceConnection,
        "Could not find service connection with name: " + serviceConnectionName);
    Map<String, Object> serviceConnectionParameters = serviceConnection.getParameters();
    ContextObject contextObject = objectApi.contextObject().set(params);
    contextObject.set("serviceConnection", serviceConnection);
    Object bodyObj = objectApi.mapper().setContext(contextObject).mapping(body).execute();

    Object headerObj = objectApi.mapper().setContext(contextObject).mapping(header).execute();
    Map<String, String> headerMap = objectApi.asMap(String.class, objectApi.toMapObject(headerObj));

    RestClient restClient = RestClient.create();

    // TODO HttpMethod parameter, contentType? uri endpoint or endpoint + path param. Headers?
    /*
     * maybe bean of call params: String serviceConnectionName, String path, HttpMethod httpMethod
     * MediaType contentType,
     */

    String endpoint = serviceConnection.getEndpoint();
    RequestBodySpec request = restClient.method(httpMethod)
        .uri(URI.create(endpoint + path))
        .contentType(contentType)
        .body(bodyObj)
        .headers(h -> headerMap.forEach((key, value) -> h.add(key, value)));

    ResponseEntity<Object> response = request
        .retrieve()
        .toEntity(Object.class);

    return response;
  }

  private ServiceConnection getServiceConnection(String serviceConnectionName) {
    MDMEntryApi entryApi = masterDataManagementApi.getApi(
        Invocations.INVOCATION_SCHEME, InvocationApiMdmConfig.MDM_ENTRY_SERVICECONNECTION);
    return objectApi.asType(ServiceConnection.class,
        entryApi.lookup().findByUnique(new ObjectPropertyValue()
            .addPathItem(ServiceConnection.NAME)
            .value(serviceConnectionName)));
  }
}
