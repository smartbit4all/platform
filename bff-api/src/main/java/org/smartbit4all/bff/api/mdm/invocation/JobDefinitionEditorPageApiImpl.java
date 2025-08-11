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
import org.smartbit4all.api.invocation.bean.InvocationRun;
import org.smartbit4all.api.invocation.bean.JobDefinition;
import org.smartbit4all.api.invocation.bean.JobParameter;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
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

public class JobDefinitionEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements JobDefinitionEditorPageApi {

  private static final Logger log =
      LoggerFactory.getLogger(JobDefinitionEditorPageApiImpl.class);

  private static final ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

  private static final String TASK_AS_STRING = "TASK_STRING";

  private static final String PARAM_AS_STRING = "PARAM_STRING";


  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public Object initModel(View view) {

    Object initModel = super.initModel(view);
    Map<String, Object> model = objectApi.asType(Map.class, initModel);

    String taskAsString = convertJsonToString(model, JobDefinition.TASK);
    String paramsAsString = convertJsonToString(model, JobDefinition.PARAMETERS);

    model.put(TASK_AS_STRING, taskAsString);
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
                    JobDefinition.CODE))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(
                    JobDefinition.NAME))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(TASK_AS_STRING))
                .enabled(true)
                .mandatory(true)
                .visible(true),
            new ComponentConstraint()
                .dataName(widgetKey(PARAM_AS_STRING))
                .enabled(true)
                .mandatory(true)
                .visible(true)));
    view.constraint(viewConstraint);
  }

  private void initLayout(View view) {
    SmartComponentLayoutDefinition layout =
        ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
            .addComponentsItem(form(LayoutDirection.VERTICAL,
                ObjectLayoutBuilder.label("label",
                    localeSettingApi.get("jobDefinition.editor.title")),
                ObjectLayoutBuilder.textfield(
                    widgetKey(
                        JobDefinition.CODE),
                    localeSettingApi.get(
                        JobDefinition.class.getName(),
                        JobDefinition.CODE)),
                ObjectLayoutBuilder.textfield(
                    widgetKey(JobDefinition.NAME),
                    localeSettingApi.get(
                        JobDefinition.class.getName(),
                        JobDefinition.NAME)),
                ObjectLayoutBuilder.textfield(
                    widgetKey(JobDefinition.DESCRIPTION),
                    localeSettingApi.get(
                        JobDefinition.class.getName(),
                        JobDefinition.DESCRIPTION)),
                ObjectLayoutBuilder.textbox(
                    widgetKey(TASK_AS_STRING),
                    localeSettingApi.get(
                        JobDefinition.class.getName(),
                        JobDefinition.TASK)),
                ObjectLayoutBuilder.textbox(
                    widgetKey(PARAM_AS_STRING),
                    localeSettingApi.get(
                        JobDefinition.class.getName(),
                        JobDefinition.PARAMETERS))));

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

    final var invocationRunAsStr = String.valueOf(m.remove(TASK_AS_STRING));
    if (invocationRunAsStr == null) {
      m.remove(JobDefinition.TASK);
    } else {
      InvocationRun invocationRun = objectApi.fromString(invocationRunAsStr, InvocationRun.class);
      m.put(JobDefinition.TASK, invocationRun);
    }

    final var paramsAsStr = String.valueOf(m.remove(PARAM_AS_STRING));
    if (!StringUtils.hasText(paramsAsStr)) {
      m.put(ScheduledJobDefinition.PARAMETERS, Collections.emptyList());
    } else {
      try {
        List<JobParameter> jobParams = objectMapper.readValue(
            paramsAsStr,
            new TypeReference<List<JobParameter>>() {});
        m.put(JobDefinition.PARAMETERS, jobParams);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Cannot save JobDefinition", e);
      }
    }

    setModel(viewUuid, m);
    super.performSave(viewUuid, request);
  }

}
