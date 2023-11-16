package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.setting.LocaleSettingApi;
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
    this.layoutsByName = layoutsByName == null
        ? new HashMap<>()
        : layoutsByName;
    this.componentConstraints = componentConstraints == null
        ? new ArrayList<>()
        : componentConstraints;
    this.uiActionConstraints = uiActionConstraints == null
        ? new ArrayList<>()
        : uiActionConstraints;
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
    return this.layoutsByName.containsKey(ObjectLayoutApi.DEFAULT_LAYOUT);
  }

  public SmartComponentLayoutDefinition getDefaultLayout() {
    return this.layoutsByName.get(ObjectLayoutApi.DEFAULT_LAYOUT);
  }

  public SmartComponentLayoutDefinition getDefaultLayout(LocaleSettingApi localeSettingApi) {
    final SmartComponentLayoutDefinition layout = getDefaultLayout();
    formWidgets(layout).forEach(w -> w.setLabel(localeSettingApi.get(w.getLabel())));
    return layout;
  }


  /**
   * Returns a list of value set names visible on the layout.
   *
   * @return a {@code List} of {@code String} value set names present and visible on the layout
   */
  public List<String> getDefaultLayoutValueSets() {
    return getLayoutValueSets(ObjectLayoutApi.DEFAULT_LAYOUT);
  }

  /**
   * Returns a list of value set names visible on the layout.
   *
   * @param layoutName the {@code String} name of a layout present on this instance, not null
   * @return a {@code List} of {@code String} value set names present and visible on the layout
   */
  public List<String> getLayoutValueSets(String layoutName) {
    if (!layoutsByName.containsKey(layoutName)) {
      return Collections.emptyList();
    }

    return formWidgets(layoutsByName.get(layoutName))
        .filter(w -> SmartFormWidgetType.SELECT == w.getType())
        // .filter(w -> componentConstraints.stream()
        // .noneMatch(c -> Objects.equals(c.getDataName(), w.getKey())
        // && Boolean.FALSE.equals(c.getVisible())))
        .map(SmartWidgetDefinition::getSelection)
        .filter(Objects::nonNull)
        .map(SelectionDefinition::getValueSetName)
        .filter(Objects::nonNull)
        .distinct()
        .collect(toList());
  }

  private Stream<SmartWidgetDefinition> formWidgets(SmartComponentLayoutDefinition compLayoutDef) {
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
            : subComponents.stream().flatMap(this::formWidgets);
      default:
        return Stream.empty();
    }
  }

  /**
   * Returns every form present in a layout in the order of their appearance.
   *
   * @param layoutName the unique {@String} name of a given layout in this object display, not null
   * @return a {@code List} of {@link SmartLayoutDefinition}s describing
   */
  public List<SmartLayoutDefinition> getForms(String layoutName) {
    if (!layoutsByName.containsKey(layoutName)) {
      return Collections.emptyList();
    }

    return forms(layoutsByName.get(layoutName))
        .map(layout -> new SmartLayoutDefinition().widgets(layout.getForm()))
        .collect(toList());
  }

  public List<SmartLayoutDefinition> getDefaultForms() {
    return getForms(ObjectLayoutApi.DEFAULT_LAYOUT);
  }

  private Stream<SmartComponentLayoutDefinition> forms(
      SmartComponentLayoutDefinition compLayoutDef) {
    if (compLayoutDef == null) {
      return Stream.empty();
    }

    switch (compLayoutDef.getType()) {
      case FORM:
        return Stream.of(compLayoutDef);
      case CONTAINER:
        return compLayoutDef.getComponents().stream().flatMap(this::forms);
      default:
        return Stream.empty();
    }
  }

  public List<String> getGridIdentifiers(String layoutName) {
    if (!layoutsByName.containsKey(layoutName)) {
      return Collections.emptyList();
    }

    return grids(layoutsByName.get(layoutName)).collect(toList());
  }

  public List<String> getDefaultGridIdentifiers() {
    return getGridIdentifiers(ObjectLayoutApi.DEFAULT_LAYOUT);
  }

  private Stream<String> grids(SmartComponentLayoutDefinition compLayoutDef) {
    if (compLayoutDef == null) {
      return Stream.empty();
    }

    switch (compLayoutDef.getType()) {
      case WIDGET:
        // Java 9+ would enable a more succinct way, but alas...
        final Optional<String> opt = Optional.ofNullable(compLayoutDef
            .getWidget()
            .getGridIdentifier());
        return !opt.isPresent() ? Stream.empty() : Stream.of(opt.get());
      case CONTAINER:
        return compLayoutDef.getComponents().stream().flatMap(this::grids);
      default:
        return Stream.empty();
    }
  }

}
