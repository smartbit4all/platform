package org.smartbit4all.api.invocation.restclient;

import java.util.Map;
import org.smartbit4all.api.invocation.bean.ResponseEntityObject;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;

public interface DynamicRestCallerApi {

  ResponseEntityObject callDynamicRest(String serviceConnectionName,
      String path,
      String httpMethodString,
      String contentType,
      ObjectMappingDefinition header,
      ObjectMappingDefinition queryParams,
      ObjectMappingDefinition body,
      Map<String, Object> params);
}
