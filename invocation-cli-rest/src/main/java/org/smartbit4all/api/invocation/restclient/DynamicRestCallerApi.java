package org.smartbit4all.api.invocation.restclient;

import java.util.Map;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface DynamicRestCallerApi {

  ResponseEntity<Object> callDynamicRest(String serviceConnectionName,
      String path,
      HttpMethod httpMethod,
      MediaType contentType,
      ObjectMappingDefinition header,
      ObjectMappingDefinition body,
      Map<String, Object> params);
}
