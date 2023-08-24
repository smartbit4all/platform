package org.smartbit4all.core.object;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.DEFAULT_LAYOUT;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
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

  public boolean isDefaultLayoutPresent() {
    return this.layoutsByName.containsKey(DEFAULT_LAYOUT);
  }

  public SmartComponentLayoutDefinition getDefaultLayout() {
    return this.layoutsByName.get(DEFAULT_LAYOUT);
  }

  /**
   * Returns a list of value set names visible on the layout.
   * 
   * @return a {@code List} of {@code String} value set names present and visible on the layout
   */
  public List<String> getDefaultLayoutValueSets() {
    return getLayoutValueSets(DEFAULT_LAYOUT);
  }

  /**
   * Returns a list of value set names visible on the layout.
   * 
   * @param layoutName the {@code String} name of a layout present on this instance, not null
   * @return a {@code List} of {@code String} value set names present and visible on the layout
   */
  public List<String> getLayoutValueSets(String layoutName) {
    if (layoutsByName.containsKey(layoutName)) {
      Collections.emptyList();
    }

    return formWidgets(layoutsByName.get(layoutName))
        .filter(w -> SmartFormWidgetType.SELECT == w.getType())
        .filter(w -> componentConstraints.stream()
            .noneMatch(c -> Objects.equals(c.getDataName(), w.getKey())
                && Boolean.FALSE.equals(c.getVisible())))
        .map(SmartWidgetDefinition::getSelection)
        .filter(Objects::nonNull)
        .map(SelectionDefinition::getValueSetName)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  private Stream<SmartWidgetDefinition> formWidgets(SmartComponentLayoutDefinition compLayoutDef) {
    if (compLayoutDef == null) {
      return Stream.empty();
    }

    switch (compLayoutDef.getType()) {
      case FORM:
        return compLayoutDef.getForm().stream();
      case CONTAINER:
        return compLayoutDef.getComponents().stream().flatMap(this::formWidgets);
      default:
        return Stream.empty();
    }
  }

}
