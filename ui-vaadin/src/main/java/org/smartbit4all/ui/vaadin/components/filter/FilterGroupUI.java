package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.common.filter.FilterGroupUIState;
import org.smartbit4all.ui.vaadin.util.IconSize;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterGroupUI extends FlexLayout {


  private FilterGroupUI parentGroupUI;
  private FlexLayout iconLayout;
  private FlexLayout filtersLayout;

  public FilterGroupUI(FilterGroupUIState uiState, FilterGroupUI parentGroupUI) {
    // TODO maybe we can skip isRoot parameter and use: isRoot = parentGroupUI == null;
    this.parentGroupUI = parentGroupUI;
    iconLayout = new FlexLayout();
    iconLayout.addClassName("icon-layout");
    add(iconLayout);

    filtersLayout = new FlexLayout();
    filtersLayout.addClassName("filters-group-layout");
    add(filtersLayout);

    if (uiState.isRoot()) {
      addClassName("dynamic-filtergroup-root");
    } else {
      addClassName("dynamic-filtergroup");
      setIconLayout(uiState.getIconCode(), uiState.getLabelCode());
    }

    if (uiState.isCloseable()) {
      // TODO handle isCloseable, add close button if true.
    }
  }

  public FilterGroupUI(boolean isRoot, FilterGroupUI parentGroupUI,
      boolean isClosable) {
    // TODO maybe we can skip isRoot parameter and use: isRoot = parentGroupUI == null;
    this.parentGroupUI = parentGroupUI;
    iconLayout = new FlexLayout();
    iconLayout.addClassName("icon-layout");
    add(iconLayout);

    filtersLayout = new FlexLayout();
    filtersLayout.addClassName("filters-group-layout");
    add(filtersLayout);

    if (isRoot) {
      addClassName("dynamic-filtergroup-root");
    } else {
      addClassName("dynamic-filtergroup");
    }

    // TODO handle isCloseable, add close button if true.
  }

  public FilterGroupUI getParentGroupUI() {
    return parentGroupUI;
  }

  public void setIconLayout(String icon, String label) {
    iconLayout.removeAll();
    VaadinIcon vaadinIcon = VaadinIcon.valueOf(icon.toUpperCase());
    Icon filterIcon = UIUtils.createIcon(IconSize.S, TextColor.TERTIARY, vaadinIcon);
    filterIcon.addClassName("filter-icon");
    Label filterLabel = UIUtils.createH4Label(getTranslation(label));
    filterLabel.addClassName("filter-label");
    iconLayout.add(filterIcon, filterLabel);
  }

  public void addToFilterGroup(FilterFieldUI filter) {
    filtersLayout.add(filter);
  }

  public FlexLayout getFiltersLayout() {
    return filtersLayout;
  }


}
