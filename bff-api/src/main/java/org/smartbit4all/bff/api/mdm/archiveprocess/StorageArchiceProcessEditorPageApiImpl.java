package org.smartbit4all.bff.api.mdm.archiveprocess;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textbox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfieldNumber;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.toggle;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.bff.api.mdm.util.MDMPropertyValueConverterUtil;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class StorageArchiceProcessEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements StorageArchiceProcessEditorPageApi {
  private static final Logger log =
      LoggerFactory.getLogger(StorageArchiceProcessEditorPageApiImpl.class);
  private static final String STORAGE_ARCHIVE_CLASS_NAME =
      StorageArchiveProcessConfig.class.getSimpleName();

  private static final ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

  @Autowired
  LocaleSettingApi localeSettingApi;

  @Override
  public Object initModel(View view) {
    Object modelMap = super.initModel(view);
    StorageArchiveProcessConfig model =
        objectApi.asType(StorageArchiveProcessConfig.class, modelMap);

    if (ObjectUtils.isEmpty(model.getTypeClassNames())) {
      model.typeClassNames(new ArrayList<>());
    } else {
      modelMap = MDMPropertyValueConverterUtil.objListStringValueConvertToString(modelMap,
          StorageArchiveProcessConfig.TYPE_CLASS_NAMES, objectApi, ";");
    }

    Map<String, Object> map = objectApi.asType(Map.class, modelMap);
    Object objectPredicateObject = map.get(StorageArchiveProcessConfig.OBJECT_PREDICATE);
    Object versionSelectorObject = map.get(StorageArchiveProcessConfig.VERSION_SELECTOR);

    ObjectWriter prettyPrinter = objectMapper.writerWithDefaultPrettyPrinter();

    try {
      map.put(StorageArchiveProcessConfig.OBJECT_PREDICATE,
          prettyPrinter.writeValueAsString(objectPredicateObject));
      map.put(StorageArchiveProcessConfig.VERSION_SELECTOR,
          prettyPrinter.writeValueAsString(versionSelectorObject));
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }

    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, getLayout());
    return map;
  }



  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {
    ObjectMapHelper params = actionRequestHelper(request);
    Map<String, Object> modelMap =
        params.get("model", Map.class);

    Object objectPredicateObject = modelMap.get(StorageArchiveProcessConfig.OBJECT_PREDICATE);
    Object versionSelectorObject = modelMap.get(StorageArchiveProcessConfig.VERSION_SELECTOR);

    if (!ObjectUtils.isEmpty(objectPredicateObject)) {
      modelMap.put(StorageArchiveProcessConfig.OBJECT_PREDICATE,
          objectApi.fromString(objectPredicateObject.toString(), InvocationRequest.class));
    }

    if (!ObjectUtils.isEmpty(versionSelectorObject)) {
      modelMap.put(StorageArchiveProcessConfig.VERSION_SELECTOR,
          objectApi.fromString(versionSelectorObject.toString(), InvocationRequest.class));
    }

    String possibleValues =
        ObjectUtils.isEmpty(modelMap.get(StorageArchiveProcessConfig.TYPE_CLASS_NAMES)) ? ""
            : (String) modelMap.get(StorageArchiveProcessConfig.TYPE_CLASS_NAMES);
    Object updatedModel;
    if (!ObjectUtils.isEmpty(possibleValues)) {
      updatedModel =
          MDMPropertyValueConverterUtil.objStringValueConvertToListString(modelMap,
              StorageArchiveProcessConfig.TYPE_CLASS_NAMES, "\\s*;\\s*");
    } else {
      modelMap.put(StorageArchiveProcessConfig.TYPE_CLASS_NAMES, new ArrayList<>());
      updatedModel = modelMap;
    }



    params.put(UiActions.MODEL, updatedModel);
    super.performSave(viewUuid, request);
  }



  private SmartComponentLayoutDefinition getLayout() {
    return ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
        .addComponentsItem(ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
            _label("storage.archive.configuration"),
            textField(StorageArchiveProcessConfig.CODE),
            textField(StorageArchiveProcessConfig.STORAGE),
            textBox(StorageArchiveProcessConfig.TYPE_CLASS_NAMES),
            textFieldNumber(StorageArchiveProcessConfig.BEFORE_DURATION_IN_MILLIS),
            _toggle(StorageArchiveProcessConfig.ARCHIVE_VERSIONS_ONLY),
            textBox(StorageArchiveProcessConfig.OBJECT_PREDICATE),
            textBox(StorageArchiveProcessConfig.VERSION_SELECTOR),
            textField(StorageArchiveProcessConfig.CRON_EXPRESSION)));
  }

  private SmartWidgetDefinition _label(String label) {
    return label(label, label);
  }

  private SmartWidgetDefinition textField(String key) {
    return textfield(key, localeSettingApi.get(STORAGE_ARCHIVE_CLASS_NAME, key));
  }

  private SmartWidgetDefinition textBox(String key) {
    return textbox(key, localeSettingApi.get(STORAGE_ARCHIVE_CLASS_NAME, key));
  }

  private SmartWidgetDefinition textFieldNumber(String key) {
    return textfieldNumber(key, localeSettingApi.get(STORAGE_ARCHIVE_CLASS_NAME, key));
  }

  private SmartWidgetDefinition _toggle(String key) {
    return toggle(key, localeSettingApi.get(STORAGE_ARCHIVE_CLASS_NAME, key));
  }
}
