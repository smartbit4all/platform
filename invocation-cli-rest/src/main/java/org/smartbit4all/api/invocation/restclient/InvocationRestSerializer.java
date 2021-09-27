package org.smartbit4all.api.invocation.restclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.InvocationParameter;
import org.smartbit4all.api.invocation.InvocationParameter.Kind;
import org.smartbit4all.api.invocation.InvocationRequest;
import org.smartbit4all.api.invocation.model.InvocationParameterData;
import org.smartbit4all.api.invocation.model.InvocationParameterKind;
import org.smartbit4all.api.invocation.model.InvocationRequestData;
import org.springframework.beans.factory.InitializingBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvocationRestSerializer implements InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(InvocationRestSerializer.class);

  private static InvocationRestSerializer instance;

  private ObjectMapper objectMapper;

  InvocationRestSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public static InvocationRequestData invocationRequest2Data(InvocationRequest request) {

    InvocationRequestData requestData = new InvocationRequestData()
        .uuid(request.getUuid())
        .executionApi(request.getExecutionApi())
        .apiClass(request.getApiClassName())
        .innerApi(request.getInnerApi())
        .methodName(request.getMethodName());

    request.getParameters().forEach(invParam -> {
      InvocationParameterKind kind = InvocationParameterKind.valueOf(invParam.getKind().name());
      String value = invParam.getStringValue();
      if (value == null && invParam.getValue() != null) {
        try {
          value = instance.objectMapper.writeValueAsString(invParam.getValue());
        } catch (JsonProcessingException e) {
          log.error("Unable to serialize object {}", invParam.getValue().getClass().getName(), e);
        }
      }
      InvocationParameterData ipd = new InvocationParameterData()
          .typeClass(invParam.getTypeClass())
          .kind(kind)
          .value(value);

      requestData.addParametersItem(ipd);
    });

    return requestData;
  }

  public static InvocationParameter invocationParameterData2Parameter(
      InvocationParameterData parameterData) {

    InvocationParameter invocationParameter = new InvocationParameter();

    String typeClass = parameterData.getTypeClass();
    String stringValue = parameterData.getValue();
    Kind kind = Kind.valueOf(parameterData.getKind().name());

    Object value = null;
    if (stringValue != null && !stringValue.isEmpty()) {
      try {
        if (String.class.getName().equals(typeClass)) {
          value = stringValue;
        } else {
          Class<?> clazz = Class.forName(typeClass);
          value = instance.objectMapper.readValue(stringValue, clazz);
        }
      } catch (Exception e) {
        log.error("Unable to deserialize object. Class: {}, stringValue: {}", typeClass,
            stringValue, e);
      }
    }

    invocationParameter.setKind(kind);
    invocationParameter.setTypeClass(typeClass);
    invocationParameter.setStringValue(stringValue);
    invocationParameter.setValue(value);

    return invocationParameter;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = this;
  }

}
