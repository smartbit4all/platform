package org.smartbit4all.api.view.layout;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * The implementation of the api.
 * 
 * @author Peter Boros
 */
public class SmartLayoutApiImpl implements SmartLayoutApi {

  @Autowired
  private LocaleSettingApi localeApi;

  @Override
  public SmartLayoutDefinition createLayout(ObjectDefinitionData objectDefinitionData,
      List<String> propertiesToInclude) {
    SmartLayoutDefinition result = new SmartLayoutDefinition();
    if (propertiesToInclude == null) {
      // return an empty layout
      return result;
    }
    Map<String, PropertyDefinitionData> propertiesByName = objectDefinitionData.getProperties()
        .stream().collect(toMap(PropertyDefinitionData::getName, p -> p));
    return result.widgets(propertiesToInclude.stream()
        .map(p -> getWidget(p, propertiesByName.get(p))).filter(w -> w != null).collect(toList()));
  }

  private final SmartWidgetDefinition getWidget(String qualifiedName,
      PropertyDefinitionData propertyDefinitionData) {
    if (propertyDefinitionData == null) {
      return null;
    }
    if (propertyDefinitionData.getWidget() != null) {
      return propertyDefinitionData.getWidget();
    }
    return new SmartWidgetDefinition().key(propertyDefinitionData.getName())
        .label(localeApi.get(qualifiedName, propertyDefinitionData.getName()))
        .type(SmartFormWidgetType.TEXT_FIELD).showLabel(true);
  }

}
