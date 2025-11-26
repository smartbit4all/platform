package org.smartbit4all.bff.api.mdm.invocation;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.JobParameter;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition.ExecutionScopeEnum;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ScheduledJobDefinitionEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements ScheduledJobDefinitionEditorPageApi {

  private static final Logger log =
      LoggerFactory.getLogger(ScheduledJobDefinitionEditorPageApiImpl.class);

  private static final ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

  private static final String PARAM_AS_STRING = "PARAM_STRING";


  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public Object initModel(View view) {

    Object initModel = super.initModel(view);
    Map<String, Object> model = objectApi.asType(Map.class, initModel);

    String paramsAsString = convertJsonToString(model, ScheduledJobDefinition.PARAMETERS);

    model.put(PARAM_AS_STRING, paramsAsString);
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
                    ScheduledJobDefinition.CODE))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(
                    ScheduledJobDefinition.NAME))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(
                    ScheduledJobDefinition.CRON_EXPRESSION))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(
                    ScheduledJobDefinition.EXECUTION_SCOPE))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(
                    ScheduledJobDefinition.JOB_DEFINITION_CODE))
                .enabled(true)
                .mandatory(false)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(
                    ScheduledJobDefinition.USER_NAME))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(PARAM_AS_STRING))
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
                        ScheduledJobDefinition.CODE),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.CODE)),
                ObjectLayoutBuilder.textfield(
                    widgetKey(ScheduledJobDefinition.NAME),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.NAME)),
                ObjectLayoutBuilder.textfield(
                    widgetKey(ScheduledJobDefinition.DESCRIPTION),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.DESCRIPTION)),
                ObjectLayoutBuilder.textfield(
                    widgetKey(ScheduledJobDefinition.CRON_EXPRESSION),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.CRON_EXPRESSION)),
                ObjectLayoutBuilder.combobox(
                    widgetKey(ScheduledJobDefinition.EXECUTION_SCOPE),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.EXECUTION_SCOPE),
                    ExecutionScopeEnum.class, localeSettingApi),
                ObjectLayoutBuilder.textfield(
                    widgetKey(ScheduledJobDefinition.JOB_DEFINITION_CODE),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.JOB_DEFINITION_CODE)),
                ObjectLayoutBuilder.textfield(
                    widgetKey(ScheduledJobDefinition.USER_NAME),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.USER_NAME)),
                ObjectLayoutBuilder.textbox(
                    widgetKey(PARAM_AS_STRING),
                    localeSettingApi.get(
                        ScheduledJobDefinition.class.getName(),
                        ScheduledJobDefinition.PARAMETERS))));

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


    final var paramsAsStr = String.valueOf(m.remove(PARAM_AS_STRING));


    if (!StringUtils.hasText(paramsAsStr)) {
      m.put(ScheduledJobDefinition.PARAMETERS, Collections.emptyList());
    } else {
      try {
        List<JobParameter> jobParams = objectMapper.readValue(
            paramsAsStr,
            new TypeReference<List<JobParameter>>() {});
        m.put(ScheduledJobDefinition.PARAMETERS, jobParams);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Cannot save ScheduledJobDefinition", e);
      }
    }
    setModel(viewUuid, m);
    super.performSave(viewUuid, request);
  }

}
