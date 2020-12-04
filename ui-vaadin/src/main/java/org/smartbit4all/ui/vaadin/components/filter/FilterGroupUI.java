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

    if (uiState.isVisible()) {
      addClassName("filtergroup");
      setIconLayout(uiState.getIconCode(), uiState.getLabelCode());
    } else {
      addClassName("filtergroup-transparent");
    }

    if (uiState.isCloseable()) {
      // TODO handle isCloseable, add close button if true.
    }
  }

  public FilterGroupUI getParentGroupUI() {
    return parentGroupUI;
  }

  public void setIconLayout(String icon, String label) {
    iconLayout.removeAll();
    if (icon != null && icon.length() > 0) {
      VaadinIcon vaadinIcon = VaadinIcon.valueOf(icon.toUpperCase());
      Icon filterIcon = UIUtils.createIcon(IconSize.S, TextColor.TERTIARY, vaadinIcon);
      filterIcon.addClassName("filter-icon");
      iconLayout.add(filterIcon);
    }
    Label filterLabel = UIUtils.createH4Label(getTranslation(label));
    filterLabel.addClassName("filter-label");
    iconLayout.add(filterLabel);
  }

  public void addToFilterGroup(FilterFieldUI filter) {
    // TODO don't add if it is already there..
    filtersLayout.add(filter);
  }

  public FlexLayout getFiltersLayout() {
    return filtersLayout;
  }


}
