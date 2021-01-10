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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterView;
import org.smartbit4all.ui.common.filter.FilterFieldUIState;
import org.smartbit4all.ui.common.filter.FilterGroupUIState;
import org.smartbit4all.ui.common.filter.FilterSelectorGroupUIState;
import org.smartbit4all.ui.common.filter.FilterSelectorUIState;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/components/dynamic-filter-view.css")
public class DynamicFilterViewUI implements DynamicFilterView {

  private DynamicFilterController controller;
  private HasComponents filterSelectorHolder;
  private HasComponents filterHolder;

  private Map<String, FilterGroupUI> groupsById = new HashMap<>();
  private Map<String, FilterFieldUI> filtersById = new HashMap<>();
  private Map<String, FilterSelectorGroupUI> selectorGroupsById = new HashMap<>();
  private Map<String, FilterSelectorUI> selectorsById = new HashMap<>();

  public DynamicFilterViewUI(DynamicFilterController controller, HasComponents filterSelectorHolder,
      HasComponents filterHolder) {
    this.controller = controller;
    this.filterSelectorHolder = filterSelectorHolder;
    this.filterHolder = filterHolder;
    controller.setUi(this);
  }

  @Override
  public void init() {
    controller.loadData();
  }

  @Override
  public void renderFilterSelectors(List<FilterSelectorGroupUIState> filterSelectorGroups) {
    filterSelectorHolder.removeAll();
    if (filterSelectorGroups.isEmpty()) {
      // no selector available, no need to render anything
      return;
    }
    VerticalLayout filterSelectorLayout = new VerticalLayout();
    filterSelectorLayout.addClassName("filterselector-layout");
    for (FilterSelectorGroupUIState group : filterSelectorGroups) {
      if (group.isVisible()) {
        Details selectorGroupUI = createFilterSelectorGroupUI(group);
        filterSelectorLayout.add(selectorGroupUI);
        for (FilterSelectorUIState field : group.filterSelectors()) {
          selectorGroupUI.addContent(createFilterSelectorUI(field));
        }
      }
    }
    filterSelectorHolder.add(filterSelectorLayout);
  }

  @Override
  public void updateFilterSelector(FilterSelectorUIState filterSelector) {
    FilterSelectorUI selectorUI = selectorsById.get(filterSelector.getId());
    if (selectorUI == null) {
      throw new RuntimeException("FilterSelectorUI not found! (" + filterSelector.getId() + ")");
    }
    selectorUI.updateState(filterSelector);
  }

  private Details createFilterSelectorGroupUI(FilterSelectorGroupUIState filterSelectorGroup) {
    FilterSelectorGroupUI selectorGroupUI = new FilterSelectorGroupUI(filterSelectorGroup);
    selectorGroupsById.put(filterSelectorGroup.getId(), selectorGroupUI);
    return selectorGroupUI;
  }

  private Component createFilterSelectorUI(FilterSelectorUIState filterSelector) {
    String id = filterSelector.getId();
    FilterSelectorUI selectorUI =
        new FilterSelectorUI(filterSelector, () -> controller.addFilterField(id));
    selectorsById.put(id, selectorUI);
    selectorUI.setDragData(selectorUI);
    return selectorUI;
  }

  @Override
  public void renderFilter(FilterFieldUIState filterUIState) {
    FilterGroupUIState groupUIState = filterUIState.getGroup();
    FilterGroupUI groupUI = groupsById.get(groupUIState.getId());
    FilterFieldUI filterUI = filtersById.get(filterUIState.getId());
    if (groupUI == null) {
      renderGroup(groupUIState);
      groupUI = groupsById.get(groupUIState.getId());
    }
    if (filterUI == null) {
      filterUI = new FilterFieldUI(groupUI, filterUIState, controller);
      filtersById.put(filterUIState.getId(), filterUI);
    } else {
      filterUI.updateState(filterUIState);
    }
    groupUI.addToFilterGroup(filterUI);
  }

  @Override
  public void renderGroup(FilterGroupUIState groupUIState) {
    FilterGroupUI parentGroupUI;
    if (groupUIState.isRoot()) {
      parentGroupUI = null;
    } else {
      parentGroupUI = groupsById.get(groupUIState.getParentGroupId());
      if (parentGroupUI == null) {
        renderGroup(groupUIState.getParentGroup());
        parentGroupUI = groupsById.get(groupUIState.getParentGroupId());
      }
    }
    FilterGroupUI groupUI = groupsById.get(groupUIState.getId());
    if (groupUI == null) {
      groupUI = new FilterGroupUI(groupUIState, parentGroupUI, controller);
      groupsById.put(groupUIState.getId(), groupUI);
      if (parentGroupUI == null) {
        filterHolder.add(groupUI);
      } else {
        parentGroupUI.add(groupUI);
      }
    } else {
      groupUI.updateState(groupUIState);
    }
  }

  @Override
  public void removeFilter(String filterId) {
    FilterFieldUI filter = filtersById.get(filterId);
    filter.getGroup().getFiltersLayout().remove(filter);
  }

  @Override
  public void removeGroup(String groupId) {
    FilterGroupUI groupUI = groupsById.get(groupId);
    groupsById.remove(groupId);
    if (groupUI.getParentGroupUI() != null) {
      groupUI.getParentGroupUI().remove(groupUI);
    } else {
      throw new RuntimeException("Trying to remove root group UI!");
    }
  }

  @Override
  public void changeActiveGroup(FilterGroupUIState prevGroupState,
      FilterGroupUIState newGroupState) {
    groupsById.get(prevGroupState.getId()).updateState(prevGroupState);
    groupsById.get(newGroupState.getId()).updateState(newGroupState);
  }

  @Override
  public void moveFilter(String groupId, String filterId) {
    FilterFieldUI filterUI = filtersById.get(filterId);
    FilterGroupUI groupUI = groupsById.get(groupId);
    filterUI.setGroup(groupUI);
    groupUI.addToFilterGroup(filterUI);
  }


  @Override
  public void updateFilterState(FilterFieldUIState filterFieldUiState) {
    FilterFieldUI filterFieldUI = filtersById.get(filterFieldUiState.getId());
    filterFieldUI.updateState(filterFieldUiState);
  }

  @Override
  public void changeFilterGroupType(String filterGroupId, FilterGroupType type) {
    FilterGroupUI filterGroupUI = groupsById.get(filterGroupId);
    filterGroupUI.setFilterGroupType(type);
  }

}
