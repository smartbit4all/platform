package org.smartbit4all.bff.api.layoutdescriptor;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDefinitionDescriptor;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.bff.api.jsonparser.JsonParserPageApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LayoutDescriptorPageApiImpl extends MDMEntryEditPageApiImpl
    implements LayoutDescriptorPageApi {

  private final ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

  @Autowired
  private InvocationApi invocationApi;

  @Override
  public Object initModel(View view) {
    Object model = super.initModel(view);
    UiActions.add(view, new UiAction().code(ACTION_EDIT_LAYOUT).model(true),
        new UiAction().code(ACTION_EDIT_CONSTRAINT).model(true));
    return model;
  }

  @Override
  public void editLayout(UUID viewUuid, UiActionRequest request) {
    LayoutDefinitionDescriptor model =
        objectApi.asType(LayoutDefinitionDescriptor.class, extractClientModel(request));
    setModel(viewUuid, model);
    InvocationRequest callback = invocationApi.builder(LayoutDescriptorPageApi.class)
        .build(api -> api.editLayoutCallback(viewUuid, null));
    showJsonParserPage(model.getConstraint(), SmartComponentLayoutDefinition.class, callback);
  }

  @Override
  public void editLayoutCallback(UUID viewUuid, SmartComponentLayoutDefinition layoutDefinition) {
    LayoutDefinitionDescriptor layoutDescriptor =
        objectApi.asType(LayoutDefinitionDescriptor.class, getModel(viewUuid));
    layoutDescriptor.setLayout(layoutDefinition);
    setModel(viewUuid, layoutDescriptor);
  }

  @Override
  public void editConstraint(UUID viewUuid, UiActionRequest request) {
    LayoutDefinitionDescriptor model =
        objectApi.asType(LayoutDefinitionDescriptor.class, extractClientModel(request));
    setModel(viewUuid, model);
    InvocationRequest callback = invocationApi.builder(LayoutDescriptorPageApi.class)
        .build(api -> api.editConstraintCallback(viewUuid, null));
    showJsonParserPage(model.getConstraint(), ViewConstraint.class, callback);
  }

  @Override
  public void editConstraintCallback(UUID viewUuid, ViewConstraint viewConstraint) {
    LayoutDefinitionDescriptor layoutDescriptor =
        objectApi.asType(LayoutDefinitionDescriptor.class, getModel(viewUuid));
    layoutDescriptor.setConstraint(viewConstraint);
    setModel(viewUuid, layoutDescriptor);
  }

  private void showJsonParserPage(Object objectToEdit, Class<?> clazz,
      InvocationRequest callback) {
    View view = new View()
        .viewName(PlatformViewNames.JSON_PARSER_DIALOG)
        .type(ViewType.DIALOG)
        .putParametersItem(JsonParserPageApi.PARAM_OBJECT_CLASS, clazz)
        .putParametersItem(JsonParserPageApi.PARAM_OBJECT, objectToEdit)
        .putCallbacksItem(JsonParserPageApi.CALLBACK_SAVE, callback);
    viewApi.showView(view);
  }

}
