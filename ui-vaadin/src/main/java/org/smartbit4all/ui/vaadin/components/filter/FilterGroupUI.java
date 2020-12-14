package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.FilterGroupUIState;
import org.smartbit4all.ui.vaadin.util.IconSize;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DropEvent;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterGroupUI extends FlexLayout implements DropTarget<FlexLayout> {

  private static final String ACTIVE_GROUP = "active-group";

  private FilterGroupUI parentGroupUI;
  private FlexLayout iconLayout;
  private FlexLayout filtersLayout;
  private FlexLayout buttonsLayout;
  private Button btnAddChildGroup;
  private Button btnRemoveGroup;
  private String groupId;
  private DynamicFilterController controller;

  public FilterGroupUI(FilterGroupUIState uiState, FilterGroupUI parentGroupUI,
      DynamicFilterController controller) {
    // TODO maybe we can skip isRoot parameter and use: isRoot = parentGroupUI == null;
    setActive(true);
    this.parentGroupUI = parentGroupUI;
    this.groupId = uiState.getId();
    this.controller = controller;
    iconLayout = new FlexLayout();
    iconLayout.addClassName("icon-layout");

    addDropListener(createDropListener());

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



    Button btnGroupType = new Button("ÉS");
    btnGroupType.addClickListener(groupTypeChangeListener());
    UIUtils.stopClickEventPropagation(btnGroupType);

    btnAddChildGroup = new Button("+");
    btnAddChildGroup.addClickListener(addChildGroupListener());
    btnAddChildGroup.addClassName("filter-addchildgroup");
    UIUtils.stopClickEventPropagation(btnAddChildGroup);

    btnRemoveGroup = new Button("x");
    btnRemoveGroup.addClickListener(removeGroupListener());
    btnRemoveGroup.addClassName("filter-removegroup");
    UIUtils.stopClickEventPropagation(btnRemoveGroup);

    buttonsLayout = new FlexLayout();
    buttonsLayout.addClassName("filter-buttons");

    if (uiState.isGroupTypeChangeEnabled()) {
      buttonsLayout.add(btnGroupType);
    }

    FlexLayout ctrlButtonsLayout = new FlexLayout();
    if (uiState.isChildGroupAllowed()) {
      ctrlButtonsLayout.add(btnAddChildGroup);
      addClickListener(groupChangeListener());
      UIUtils.stopClickEventPropagation(this);
    }

    if (uiState.isCloseable()) {
      ctrlButtonsLayout.add(btnRemoveGroup);
    }

    if (ctrlButtonsLayout.getComponentCount() > 0) {
      buttonsLayout.add(ctrlButtonsLayout);
    }
    if (buttonsLayout.getComponentCount() > 0) {
      Div flexibleSeparator = new Div();
      flexibleSeparator.addClassName("filter-buttons-separator");
      filtersLayout.add(flexibleSeparator);
      filtersLayout.add(buttonsLayout);
    }
  }

  private ComponentEventListener<ClickEvent<Button>> groupTypeChangeListener() {
    return e -> {
      if (e.getSource().getText().equals("ÉS")) {
        e.getSource().setText("VAGY");
      } else {
        e.getSource().setText("ÉS");
      }
    };
  }

  private ComponentEventListener<DropEvent<FlexLayout>> createDropListener() {
    return e -> {
      e.getDragData().ifPresent(data -> {
        if (data instanceof FilterFieldUI) {
          controller.changeGroup(((FilterFieldUI) data).getGroup().groupId, groupId,
              ((FilterFieldUI) data).getFilterId());
        } else if (data instanceof FilterSelectorUI) {
          controller.activeFilterGroupChanged(groupId);
          controller.addFilterField(((FilterSelectorUI) data).getSelectorId());
        }
      });
    };
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
      controller.activeFilterGroupChanged(groupId);
    };
  }

  private ComponentEventListener<ClickEvent<Button>> addChildGroupListener() {
    return e -> {
      controller.addFilterGroup(groupId);
    };
  }

  private ComponentEventListener<ClickEvent<Button>> removeGroupListener() {
    return e -> {
      controller.removeFilterGroup(groupId);
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
