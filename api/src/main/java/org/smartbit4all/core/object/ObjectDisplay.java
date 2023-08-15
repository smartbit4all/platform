package org.smartbit4all.core.object;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiActionConstraint;

public final class ObjectDisplay {

  private final Map<String, SmartComponentLayoutDefinition> layoutsByName;
  private final List<ComponentConstraint> componentConstraints;
  private final List<UiActionConstraint> uiActionConstraints;

  ObjectDisplay(Map<String, SmartComponentLayoutDefinition> layoutsByName,
      List<ComponentConstraint> componentConstraints,
      List<UiActionConstraint> uiActionConstraints) {
    this.layoutsByName = layoutsByName;
    this.componentConstraints = componentConstraints;
    this.uiActionConstraints = uiActionConstraints;
  }


  public Map<String, SmartComponentLayoutDefinition> getLayoutsByName() {
    return layoutsByName;
  }


  public List<ComponentConstraint> getComponentConstraints() {
    return componentConstraints;
  }


  public List<UiActionConstraint> getUiActionConstraints() {
    return uiActionConstraints;
  }
}
