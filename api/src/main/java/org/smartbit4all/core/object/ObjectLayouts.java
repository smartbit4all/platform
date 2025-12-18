package org.smartbit4all.core.object;

import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;

public abstract class ObjectLayouts {

  public static Stream<SmartWidgetDefinition> formWidgets(
      SmartComponentLayoutDefinition compLayoutDef) {
    if (compLayoutDef == null) {
      return Stream.empty();
    }

    switch (compLayoutDef.getType()) {
      case FORM:
        return compLayoutDef.getForm().stream();
      case CONTAINER:
        final List<SmartComponentLayoutDefinition> subComponents = compLayoutDef.getComponents();
        return subComponents == null
            ? Stream.empty()
            : subComponents.stream().flatMap(ObjectLayouts::formWidgets);
      default:
        return Stream.empty();
    }
  }

  private ObjectLayouts() {}
}
