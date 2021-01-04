/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.FilterGroupUIState;
import org.smartbit4all.ui.vaadin.util.Css;
import org.smartbit4all.ui.vaadin.util.Css.IconSize;
import org.smartbit4all.ui.vaadin.util.Css.TextColor;
import org.smartbit4all.ui.vaadin.util.Icons;
import org.smartbit4all.ui.vaadin.util.Labels;
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
  private Button btnGroupType;
  private FilterGroupType groupType;

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

    groupType = FilterGroupType.AND;
    btnGroupType = new Button(groupType.getValue());
    btnGroupType.addClickListener(groupTypeChangeListener());
    Css.stopClickEventPropagation(btnGroupType);

    btnAddChildGroup = new Button("+");
    btnAddChildGroup.addClickListener(addChildGroupListener());
    btnAddChildGroup.addClassName("filter-addchildgroup");
    Css.stopClickEventPropagation(btnAddChildGroup);

    btnRemoveGroup = new Button("x");
    btnRemoveGroup.addClickListener(removeGroupListener());
    btnRemoveGroup.addClassName("filter-removegroup");
    Css.stopClickEventPropagation(btnRemoveGroup);

    buttonsLayout = new FlexLayout();
    buttonsLayout.addClassName("filter-buttons");

    if (uiState.isGroupTypeChangeEnabled()) {
      buttonsLayout.add(btnGroupType);
    }

    FlexLayout ctrlButtonsLayout = new FlexLayout();
    if (uiState.isChildGroupAllowed()) {
      ctrlButtonsLayout.add(btnAddChildGroup);
      addClickListener(groupChangeListener());
      Css.stopClickEventPropagation(this);
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
      FilterGroupType newGroupType =
          groupType == FilterGroupType.AND ? FilterGroupType.OR : FilterGroupType.AND;
      controller.changeFilterGroupType(groupId, newGroupType);
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
      Icon filterIcon = Icons.createIcon(IconSize.S, TextColor.TERTIARY, vaadinIcon);
      filterIcon.addClassName("filter-icon");
      iconLayout.add(filterIcon);
    }

    if (label != null && label.length() > 0) {
      Label filterLabel = Labels.createH4Label(getTranslation(label));
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

  public String getGroupId() {
    return groupId;
  }

  public void setFilterGroupType(FilterGroupType groupType) {
    this.groupType = groupType;
    btnGroupType.setText(groupType.getValue());
  }


}
