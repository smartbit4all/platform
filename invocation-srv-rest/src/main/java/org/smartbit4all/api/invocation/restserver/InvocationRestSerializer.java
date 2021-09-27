package org.smartbit4all.api.invocation.restserver;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.model.InvocationParameterData;
import org.smartbit4all.api.invocation.model.InvocationRequestData;
import org.springframework.beans.factory.InitializingBean;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvocationRestSerializer implements InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(InvocationRestSerializer.class);

  private static InvocationRestSerializer instance;

  private ObjectMapper objectMapper;

  public InvocationRestSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public static InvocationRequest invocationRequestData2Request(InvocationRequestData requestData) {

    InvocationRequest request = new InvocationRequest(requestData.getApiClass())
        .exec(requestData.getExecutionApi())
        .innerApi(requestData.getInnerApi())
        .method(requestData.getMethodName());

    int paramCntr = 0;
    for (InvocationParameterData invParam : requestData.getParameters()) {
      String valueString = invParam.getValue();
      String typeClassName = invParam.getTypeClass();
      Objects.requireNonNull(valueString, "valueString can not be null in InvocationRequestData!");
      Objects.requireNonNull(typeClassName, "typeClass can not be null in InvocationRequestData!");
      Object value = null;
      if (valueString != null) {
        try {
          Class<?> typeClass = Class.forName(typeClassName);
          value = instance.objectMapper.readValue(valueString, typeClass);
        } catch (Exception e) {
          log.error("Unable to deserialize object {}", typeClassName, e);
        }
      }

      request.addParameter("param" + paramCntr++, value, typeClassName);
    }

    return request;
  }

  public static InvocationParameterData invocationParameter2ParameterData(
      InvocationParameter parameter) {

    InvocationParameterData invocationParameterData = new InvocationParameterData();

    String typeClass = parameter.getTypeClass();
    Object value = parameter.getValue();
    org.smartbit4all.api.invocation.model.InvocationParameterKind kind =
        org.smartbit4all.api.invocation.model.InvocationParameterKind
            .valueOf(parameter.getKind().name());

    String stringValue = null;
    if (value != null) {
      try {
        if (String.class.getName().equals(typeClass)) {
          stringValue = value.toString();
        } else {
          stringValue = instance.objectMapper.writeValueAsString(value);
        }
      } catch (Exception e) {
        log.error("Unable to serialize object. Class: {}, value: {}", typeClass,
            value, e);
      }
    }

    invocationParameterData.setKind(kind);
    invocationParameterData.setTypeClass(typeClass);
    invocationParameterData.setValue(stringValue);

    return invocationParameterData;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = this;
  }

}
