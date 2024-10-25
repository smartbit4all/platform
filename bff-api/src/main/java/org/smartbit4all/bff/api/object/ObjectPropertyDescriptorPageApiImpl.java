package org.smartbit4all.bff.api.object;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor.PropertyKindEnum;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSetDefinitionIdentifier;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectPropertyDescriptorPageApiImpl extends PageApiImpl<ObjectPropertyDescriptor>
    implements ObjectPropertyDescriptorPageApi {

  private static final Logger log =
      LoggerFactory.getLogger(ObjectPropertyDescriptorPageApiImpl.class);

  private static final String OBJECT_PROPERTY_DESCRIPTOR =
      ObjectPropertyDescriptor.class.getSimpleName();

  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  public ObjectPropertyDescriptorPageApiImpl() {
    super(ObjectPropertyDescriptor.class);
  }

  @Override
  public ObjectPropertyDescriptor initModel(View view) {
    ObjectPropertyDescriptor model =
        parameters(view).requireNonNull(PARAM_MODEL, ObjectPropertyDescriptor.class);

    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, layout());
    UiActions.add(view, new UiAction().code(SAVE_ACTION).submit(true),
        new UiAction().code(DEFAULT_CLOSE));

    return model;

  }

  private SmartComponentLayoutDefinition layout() {
    return ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
        textfield(ObjectPropertyDescriptor.PROPERTY_NAME,
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR,
                ObjectPropertyDescriptor.PROPERTY_NAME)),
        comboboxOverride(ObjectPropertyDescriptor.PROPERTY_KIND,
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR,
                ObjectPropertyDescriptor.PROPERTY_KIND),
            fromEnumToValues(PropertyKindEnum.values())),
        textfield(ObjectPropertyDescriptor.PROPERTY_QUALIFIED_NAME,
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR,
                ObjectPropertyDescriptor.PROPERTY_QUALIFIED_NAME)),
        textfield(ObjectPropertyDescriptor.REFERENCED_TYPE_QUALIFIED_NAME,
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR,
                ObjectPropertyDescriptor.REFERENCED_TYPE_QUALIFIED_NAME)),
        comboboxOverride(ObjectPropertyDescriptor.AGGREGATION,
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR, ObjectPropertyDescriptor.AGGREGATION),
            fromEnumToValues(AggregationKind.values())),
        comboboxOverride(ObjectPropertyDescriptor.PROPERTY_STRUCTURE,
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR,
                ObjectPropertyDescriptor.PROPERTY_STRUCTURE),
            fromEnumToValues(ReferencePropertyKind.values())),
        textfield(
            widgetKey(ObjectPropertyDescriptor.VALUE_SET, ValueSetDefinitionIdentifier.NAMESPACE),
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR, ObjectPropertyDescriptor.VALUE_SET,
                ValueSetDefinitionIdentifier.NAMESPACE)),
        textfield(
            widgetKey(ObjectPropertyDescriptor.VALUE_SET,
                ValueSetDefinitionIdentifier.QUALIFIED_NAME),
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR, ObjectPropertyDescriptor.VALUE_SET,
                ValueSetDefinitionIdentifier.QUALIFIED_NAME)),
        comboboxOverride(widgetKey(ObjectPropertyDescriptor.WIDGET, SmartWidgetDefinition.TYPE),
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR, ObjectPropertyDescriptor.WIDGET,
                SmartWidgetDefinition.TYPE),
            fromEnumToValues(SmartFormWidgetType.values())),
        textfield(widgetKey(ObjectPropertyDescriptor.WIDGET, SmartWidgetDefinition.KEY),
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR, ObjectPropertyDescriptor.WIDGET,
                SmartWidgetDefinition.KEY)),
        textfield(widgetKey(ObjectPropertyDescriptor.WIDGET, SmartWidgetDefinition.LABEL),
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR, ObjectPropertyDescriptor.WIDGET,
                SmartWidgetDefinition.LABEL)),
        textfield(ObjectPropertyDescriptor.DEFAULT_VALUE,
            localeSettingApi.get(OBJECT_PROPERTY_DESCRIPTOR,
                ObjectPropertyDescriptor.DEFAULT_VALUE)));
  }

  private List<Value> fromEnumToValues(Enum<?>... en) {
    return en != null ? Stream.of(en).map(e -> new Value().code(e.toString())
        .displayValue(localeSettingApi.get(e.getClass().getSimpleName(), e.toString())))
        .collect(Collectors.toList()) : Collections.emptyList();
  }

  private SmartWidgetDefinition comboboxOverride(String key, String label, List<Value> values) {
    return new SmartWidgetDefinition().key(key).label(label).values(values);
  }

  @Override
  public void save(UUID viewUuid, UiActionRequest request) {
    ObjectPropertyDescriptor model = getModel(viewUuid);
    InvocationRequest callback = objectApi.asType(InvocationRequest.class,
        viewApi.getView(viewUuid).getCallbacks().get(CALLBACK_SAVE));
    Invocations.setParameterFirstWithType(callback, model);
    try {
      invocationApi.invoke(callback);
    } catch (ApiNotFoundException e) {
      log.error(e.getMessage(), e);
    }
    viewApi.closeView(viewUuid);
  }

}
