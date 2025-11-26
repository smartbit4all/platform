package org.smartbit4all.bff.api.mdm.archiveprocess;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.combobox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textbox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfieldNumber;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig.ModeEnum;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.bff.api.mdm.util.MDMPropertyValueConverterUtil;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.smartbit4all.domain.data.storage.StorageArchiveApi;
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
  @Autowired
  private StorageArchiveApi storageArchiveApi;

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
    Object collectionsListObject = map.get(StorageArchiveProcessConfig.COLLECTIONS);
    ObjectWriter prettyPrinter = objectMapper.writerWithDefaultPrettyPrinter();

    if (!ObjectUtils.isEmpty(collectionsListObject)) {
      List storedListDescriptors =
          objectApi.asType(List.class, collectionsListObject);
      map.put(StorageArchiveProcessConfig.COLLECTIONS,
          storedListDescriptors.stream()
              .map(descriptor -> {
                try {
                  return prettyPrinter.writeValueAsString(descriptor);
                } catch (JsonProcessingException e) {
                  log.error(e.getMessage(), e);
                  return "{}";
                }
              })
              .collect(Collectors.joining(";")));
    }

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
    Object collectionsListObject = modelMap.get(StorageArchiveProcessConfig.COLLECTIONS);


    if (!ObjectUtils.isEmpty(objectPredicateObject)) {
      modelMap.put(StorageArchiveProcessConfig.OBJECT_PREDICATE,
          objectApi.fromString(objectPredicateObject.toString(), InvocationRequest.class));
    }

    if (!ObjectUtils.isEmpty(versionSelectorObject)) {
      modelMap.put(StorageArchiveProcessConfig.VERSION_SELECTOR,
          objectApi.fromString(versionSelectorObject.toString(), InvocationRequest.class));
    }

    if (!ObjectUtils.isEmpty(collectionsListObject)) {

      String[] collectionsDescArray = collectionsListObject.toString().split("\\s*;\\s*");
      List<String> collectionsDescList = Arrays.asList(collectionsDescArray);
      modelMap.put(StorageArchiveProcessConfig.COLLECTIONS,
          collectionsDescList.stream()
              .map(string -> objectApi.fromString(string.toString(),
                  StoredCollectionDescriptor.class))
              .collect(Collectors.toList()));
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
    StorageArchiveProcessConfig config =
        objectApi.asType(StorageArchiveProcessConfig.class, updatedModel);
    if (ObjectUtils.isEmpty(config.getUri())) {
      URI savedConfig =
          storageArchiveApi.createConfig(config, viewApi.getView(viewUuid).getBranchUri());
      updatedModel = objectApi.loadLatest(savedConfig).getObjectAsMap();
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
            comboBox(StorageArchiveProcessConfig.MODE),
            textBox(StorageArchiveProcessConfig.TYPE_CLASS_NAMES),
            textFieldNumber(StorageArchiveProcessConfig.BEFORE_DURATION_IN_MILLIS),
            textBox(StorageArchiveProcessConfig.COLLECTIONS),
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

  private SmartWidgetDefinition comboBox(String key) {
    return combobox(key, localeSettingApi.get(STORAGE_ARCHIVE_CLASS_NAME, key), ModeEnum.class,
        localeSettingApi);
  }
}
