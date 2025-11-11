package org.smartbit4all.bff.api.mdm.jsoneditor;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.formdefinition.bean.FileUploaderProperties;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.MessageTextType;
import org.smartbit4all.api.view.bean.MessageType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.UiActionUploadDescriptor;
import org.smartbit4all.api.view.bean.UploadedFile;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;

public class JsonEditorPageApiImpl extends MDMEntryEditPageApiImpl implements JsonEditorPageApi {


  private static final Logger log = LoggerFactory.getLogger(JsonEditorPageApiImpl.class);

  private static final String OBJECT_AS_STRING = "OBJECT_AS_STRING";

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public Object initModel(View view) {

    Object initModel = super.initModel(view);
    Map<String, Object> model = objectApi.asType(Map.class, initModel);

    String objectAsString = "";
    if (view.getObjectUri() != null) {

      try {
        objectAsString = objectApi.definition(ObjectDefinitionData.class)
            .writeValueAsString(objectApi.loadLatest(view.getObjectUri()).getObject());
      } catch (JsonProcessingException e) {

        log.error("Error during deserialization", e);
        showError(view.getUuid());
      }
    }

    model.put(OBJECT_AS_STRING, objectAsString);
    initLayout(view);
    initConstraints(view);
    UiActions.remove(view,
        MDMEntryEditPageApi.ACTION_SAVE,
        MDMEntryEditPageApi.ACTION_CANCEL);

    UiActions.add(view,
        ACTION_SAVE_BUTTON.apply(localeSettingApi),
        ACTION_CLOSE_BUTTON.apply(localeSettingApi));

    return model;
  }

  private void initLayout(View view) {
    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
        .addComponentsItem(form(LayoutDirection.VERTICAL,
            ObjectLayoutBuilder.label("labelKey",
                localeSettingApi.get(JSON_EDITOR_TITLE)),
            ObjectLayoutBuilder.fileUpload(
                "mockFile", "label",
                new FileUploaderProperties()
                    .uploadAction(ACTION_UPLOAD.get())
                    .uploadDescriptor(new UiActionUploadDescriptor()
                        .autoUpload(true)
                        .formats(".json")
                        .maxSize("100")
                        .title(localeSettingApi.get(JSON_UPLOAD_TEXT))))
                .showLabel(false),
            ObjectLayoutBuilder.textbox(
                widgetKey(OBJECT_AS_STRING),
                localeSettingApi.get(OBJECT_AS_STRING))));

    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, layout);
  }


  private void initConstraints(View view) {

    ViewConstraint viewConstraint = new ViewConstraint()
        .componentConstraints(Arrays.asList(
            new ComponentConstraint()
                .dataName(widgetKey(OBJECT_AS_STRING))
                .enabled(true).mandatory(true).visible(true)));
    view.constraint(viewConstraint);
  }

  @Override
  public void upload(UUID viewUuid, UiActionRequest request) {

    UploadedFile uploadedFile =
        actionRequestHelper(request).get(UiActions.FILES, UploadedFile.class);

    try (InputStream inputStream = uploadedFile.getData().inputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(inputStream.readAllBytes())) {

      String objectAsString = StreamUtils.copyToString(bais, StandardCharsets.UTF_8);

      Object model = actionRequestHelper(request).get(UiActions.MODEL, Object.class);
      Map<String, Object> modelMap = objectApi.asType(Map.class, model);
      modelMap.put(OBJECT_AS_STRING, objectAsString);
      setModel(viewUuid, modelMap);

    } catch (IOException e) {
      log.error("Error during deserialization", e);
      showError(viewUuid);
    }

  }

  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {
    Object modelObj = actionRequestHelper(request)
        .getMap()
        .get(UiActions.MODEL);
    if (!(modelObj instanceof Map m)) {
      throw new IllegalAccessError();
    }

    String objectAsString = getStringValueFromModel(m, OBJECT_AS_STRING);

    Object jsonObject;
    try {
      jsonObject = objectApi.fromString(objectAsString, Object.class);
    } catch (Exception e) {
      log.error("Error during deserialization");
      showError(viewUuid);
      return;
    }

    if (jsonObject instanceof List list) {
      // Case for list of object

      List<Object> objectList = list;
      objectList.forEach(object -> {

        request.getParams().put(UiActions.MODEL, object);
        super.performSave(viewUuid, request);
      });

    } else if (jsonObject instanceof Map map) {
      // Case for single object

      request.getParams().put(UiActions.MODEL, map);
      super.performSave(viewUuid, request);
    } else {
      throw new IllegalAccessError();
    }
  }

  private String getStringValueFromModel(Map m, String key) {
    String stringValue = String.valueOf(m.remove(key));
    return Strings.isNullOrEmpty(stringValue) ? null : stringValue;
  }

  private void showError(UUID viewUuid) {
    viewApi.showMessage(new MessageData()
        .header(localeSettingApi.get(DESERIALIZATION_ERROR_TITLE))
        .text(localeSettingApi.get(DESERIALIZATION_ERROR_TEXT))
        .viewUuid(viewUuid)
        .textType(MessageTextType.HTML)
        .type(MessageType.INFO));
  }


}
