package org.smartbit4all.ui.vaadin.components.filter;

import java.util.function.Consumer;
import org.smartbit4all.ui.common.filter.FilterGroupUIState;
import org.smartbit4all.ui.vaadin.util.IconSize;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterGroupUI extends FlexLayout {

  private static final String ACTIVE_GROUP = "active-group";

  private FilterGroupUI parentGroupUI;
  private FlexLayout iconLayout;
  private FlexLayout filtersLayout;
  private Button btnAddFilter;
  private Consumer<String> activeGroupChange;
  private Consumer<String> addChildGroup;
  private String groupId;

  public FilterGroupUI(FilterGroupUIState uiState, FilterGroupUI parentGroupUI,
      Consumer<String> activeGroupChange, Consumer<String> addChildGroup) {
    // TODO maybe we can skip isRoot parameter and use: isRoot = parentGroupUI == null;
    this.parentGroupUI = parentGroupUI;
    this.activeGroupChange = activeGroupChange;
    this.addChildGroup = addChildGroup;
    this.groupId = uiState.getId();
    iconLayout = new FlexLayout();
    iconLayout.addClassName("icon-layout");

    if (uiState.isVisible()) {
      addClassName("filtergroup");
      setIconLayout(uiState.getIconCode(), uiState.getLabelCode());
    } else {
      addClassName("filtergroup-transparent");
    }

    if (uiState.isChildGroupAllowed() && !uiState.isRoot()) {
      addClassName("child-group");
    }

    if (iconLayout.getComponentCount() != 0) {
      add(iconLayout);
    }

    filtersLayout = new FlexLayout();
    filtersLayout.addClassName("filters-group-layout");
    add(filtersLayout);

    btnAddFilter = new Button("+");
    btnAddFilter.addClickListener(addFilterListener());
    btnAddFilter.getElement().addEventListener("click", e -> {
    }).addEventData("event.stopPropagation()");

    if (uiState.isChildGroupAllowed()) {
      add(btnAddFilter);
      addClickListener(groupChangeListener());
      getElement().addEventListener("click", e -> {
      }).addEventData("event.stopPropagation()");
    }

    if (uiState.isCloseable()) {
      // TODO handle isCloseable, add close button if true.
    }
  }

  public void updateState(FilterGroupUIState uiState) {
    if (uiState.isActive()) {
      addClassName(ACTIVE_GROUP);
    } else {
      removeClassName(ACTIVE_GROUP);
    }
  }

  private ComponentEventListener<ClickEvent<FlexLayout>> groupChangeListener() {
    return e -> {
      activeGroupChange.accept(groupId);
    };
  }

  private ComponentEventListener<ClickEvent<Button>> addFilterListener() {
    return e -> {
      addChildGroup.accept(groupId);
    };
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

    if (label != null && label.length() > 0) {
      Label filterLabel = UIUtils.createH4Label(getTranslation(label));
      filterLabel.addClassName("filter-label");
      iconLayout.add(filterLabel);
    }

  }

  public void addToFilterGroup(FilterFieldUI filter) {
    if (!filtersLayout.getChildren().anyMatch(child -> filter.equals(child))) {
      filtersLayout.add(filter);
    }
  }

  public FlexLayout getFiltersLayout() {
    return filtersLayout;
  }


}
