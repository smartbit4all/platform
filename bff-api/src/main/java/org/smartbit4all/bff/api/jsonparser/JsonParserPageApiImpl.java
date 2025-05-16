package org.smartbit4all.bff.api.jsonparser;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JsonParserPageApiImpl extends PageApiImpl<Object> implements JsonParserPageApi {

  private static final String OBJECT = "object";

  private final ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  public JsonParserPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    Map<String, String> model = new LinkedHashMap<>();

    ObjectMapHelper parameters = parameters(view);

    // this is for getting the information that the reader is passed in the begining
    parameters.requireNonNull(PARAM_OBJECT_CLASS, Class.class);

    Object object = parameters.get(PARAM_OBJECT, Object.class);
    String jsonObject = parameters.get(PARAM_JSON, String.class);

    if (jsonObject != null) {
      model.put(OBJECT, jsonObject);
    } else if (object != null) {
      try {
        String writtenValue = objectMapper.writeValueAsString(object);
        model.put(OBJECT, writtenValue);
      } catch (JsonProcessingException e) {
        throw new BusinessLogicException(localeSettingApi.get("error.jsonProcessing"));
      }
    } else {
      model.put(OBJECT, StringConstant.EMPTY);
    }

    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
        ObjectLayoutBuilder.textbox(OBJECT,
            localeSettingApi.get(JsonParserPageApi.class.getSimpleName(), OBJECT)));
    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, layout);
    UiActions.add(view, new UiAction().code(ACTION_SAVE).model(true),
        new UiAction().code(DEFAULT_CLOSE));

    return model;
  }

  @Override
  public void save(UUID viewUuid, UiActionRequest request) {
    @SuppressWarnings("unchecked")
    Map<String, String> model =
        objectApi.asType(Map.class, extractClientModel(request));
    String jsonString = model.get(OBJECT);
    Class<?> clazz =
        parameters(viewUuid).requireNonNull(PARAM_OBJECT_CLASS, Class.class);
    ObjectReader objectReader = objectMapper.readerFor(clazz);

    Object value;
    try {
      value = objectReader.readTree(jsonString);
    } catch (JsonProcessingException e) {
      throw new BusinessLogicException(localeSettingApi.get("error.jsonProcessing"));
    }

    InvocationRequest callback = objectApi.asType(InvocationRequest.class,
        viewApi.getView(viewUuid).getCallbacks().get(CALLBACK_SAVE));
    Invocations.setParameterFirstWithType(callback, value);
    try {
      invocationApi.invoke(callback);
    } catch (ApiNotFoundException e) {
      throw new BusinessLogicException(localeSettingApi.get("error.apiNotFoundException.simple"));
    }
  }

}
