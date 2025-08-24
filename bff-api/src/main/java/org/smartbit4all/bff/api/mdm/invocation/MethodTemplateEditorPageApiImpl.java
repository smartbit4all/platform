package org.smartbit4all.bff.api.mdm.invocation;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.MethodTemplate;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class MethodTemplateEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements MethodTemplateEditorPageApi {

  private static final Logger log =
      LoggerFactory.getLogger(MethodTemplateEditorPageApiImpl.class);

  private static final ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

  private static final String INVOCATION_REQUEST_AS_STRING = "INVOCATION_REQUEST_AS_STRING";


  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public Object initModel(View view) {

    Object initModel = super.initModel(view);
    Map<String, Object> model = objectApi.asType(Map.class, initModel);

    String requestAsString = convertJsonToString(model, MethodTemplate.REQUEST);

    model.put(INVOCATION_REQUEST_AS_STRING, requestAsString);
    initLayout(view);
    // initGrid(view, model.getItemDefinition());
    initConstraints(view);

    return model;
  }

  private String convertJsonToString(Map<String, Object> model, String field) {
    Object json = model.get(field);

    String jsonAsString = "";
    if (!ObjectUtils.isEmpty(json)) {
      try {
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        jsonAsString = writer.writeValueAsString(json);
      } catch (JsonProcessingException e) {
        log.debug("Error: {}", e);
      }
    }
    return jsonAsString;
  }


  private void initConstraints(View view) {

    ViewConstraint viewConstraint = new ViewConstraint()
        .componentConstraints(Arrays.asList(
            new ComponentConstraint()
                .dataName(widgetKey(
                    MethodTemplate.FULLY_QUALIFIED_NAME))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(
                    MethodTemplate.TEMPLATE))
                .enabled(true)
                .mandatory(false)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(INVOCATION_REQUEST_AS_STRING))
                .enabled(true)
                .mandatory(false)
                .visible(true)));
    view.constraint(viewConstraint);
  }

  private void initLayout(View view) {
    SmartComponentLayoutDefinition layout =
        ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
            .addComponentsItem(form(LayoutDirection.VERTICAL,
                ObjectLayoutBuilder.label("label",
                    localeSettingApi.get("scheduledJobDefinition.editor.title")),
                ObjectLayoutBuilder.textfield(
                    widgetKey(
                        MethodTemplate.FULLY_QUALIFIED_NAME),
                    localeSettingApi.get(
                        MethodTemplate.class.getName(),
                        MethodTemplate.FULLY_QUALIFIED_NAME)),
                ObjectLayoutBuilder.textfield(
                    widgetKey(MethodTemplate.TEMPLATE),
                    localeSettingApi.get(
                        MethodTemplate.class.getName(),
                        MethodTemplate.TEMPLATE)),
                ObjectLayoutBuilder.textbox(
                    widgetKey(INVOCATION_REQUEST_AS_STRING),
                    localeSettingApi.get(
                        MethodTemplate.class.getName(),
                        MethodTemplate.REQUEST))));

    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, layout);
  }

  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {

    Object modelObj = actionRequestHelper(request)
        .getMap()
        .get(UiActions.MODEL);
    if (!(modelObj instanceof Map m)) {
      throw new IllegalAccessError();
    }


    final var requestAsStr = String.valueOf(m.remove(INVOCATION_REQUEST_AS_STRING));

    if (StringUtils.hasText(requestAsStr)) {
      InvocationRequest invocationRequest =
          objectApi.fromString(requestAsStr, InvocationRequest.class);
      m.put(MethodTemplate.REQUEST, invocationRequest);
    } else {
      m.remove(MethodTemplate.REQUEST);
    }

    setModel(viewUuid, m);
    super.performSave(viewUuid, request);
  }

}
