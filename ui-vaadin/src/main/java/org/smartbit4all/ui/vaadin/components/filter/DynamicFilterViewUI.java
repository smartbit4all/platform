package org.smartbit4all.ui.vaadin.components.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterView;
import org.smartbit4all.ui.common.filter.FilterFieldUIState;
import org.smartbit4all.ui.common.filter.FilterGroupUIState;
import org.smartbit4all.ui.common.filter.FilterSelectorGroupUIState;
import org.smartbit4all.ui.common.filter.FilterSelectorUIState;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/components/dynamic-filter-view.css")
public class DynamicFilterViewUI implements DynamicFilterView {

  private DynamicFilterController controller;
  private HasComponents filterSelectorHolder;
  private HasComponents filterHolder;

  private Map<String, DynamicFilterGroupUI> groupsById = new HashMap<>();
  private Map<String, DynamicFilterUI> filtersById = new HashMap<>();

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
    if (filterSelectorGroups.isEmpty()) {
      // no selector available, no need to render anything
      return;
    }
    VerticalLayout filterSelectorLayout = new VerticalLayout();
    filterSelectorLayout.addClassName("filterselector-layout");
    for (FilterSelectorGroupUIState group : filterSelectorGroups) {
      Details selectorGroupUI = createFilterSelectorGroupUI(group);
      filterSelectorLayout.add(selectorGroupUI);
      for (FilterSelectorUIState field : group.filterSelectors()) {
        selectorGroupUI.addContent(createFilterSelectorUI(field));
      }
    }
    filterSelectorHolder.add(filterSelectorLayout);
  }

  private Details createFilterSelectorGroupUI(FilterSelectorGroupUIState filterSelectorGroup) {
    Details details = new Details();
    details.addThemeVariants(DetailsVariant.FILLED);
    Label summaryText = new Label(details.getTranslation(filterSelectorGroup.getLabelCode()));
    // Button btnChooseGroup = new Button(new Icon(VaadinIcon.PLUS));
    // btnChooseGroup.addClickListener(groupSelectButtonListener());

    FlexLayout summary = new FlexLayout(summaryText);

    details.setSummary(summary);
    details.setOpened(true);
    return details;

  }

  private Component createFilterSelectorUI(FilterSelectorUIState filterSelector) {
    Label label = new Label();
    label.setText(label.getTranslation(filterSelector.getLabelCode()));
    Button button = new Button(new Icon(VaadinIcon.PLUS));
    button.addClickListener(addFilterSelectorListener(filterSelector.getId()));
    FlexLayout filterMetaUI = new FlexLayout(label, button);
    filterMetaUI.addClassName("filtermeta-layout");
    // filterMetaUI.addClassName("descriptor-layout");
    return filterMetaUI;
  }

  private ComponentEventListener<ClickEvent<Button>> addFilterSelectorListener(
      String filterSelectorId) {
    return e -> {
      controller.addFilter(filterSelectorId);
    };
  }

  @Override
  public void renderFilter(FilterFieldUIState filterUIState, List<Value> possibleValues) {
    FilterGroupUIState groupUIState = filterUIState.getGroup();
    DynamicFilterGroupUI groupUI = groupsById.get(groupUIState.getId());
    DynamicFilterUI filterUI = filtersById.get(filterUIState.getId());
    if (filterUI != null) {
      throw new IllegalArgumentException(
          "Existing filterUI found when creating new filterUI with '" + filterUIState.getId()
              + "' filterId!");
    }
    if (groupUI == null) {
      renderGroup(groupUIState);
      groupUI = groupsById.get(groupUIState.getId());
    }
    filterUI = new DynamicFilterUI(groupUI, filterUIState,
        () -> controller.removeFilter(groupUIState.getId(), filterUIState.getId()));
    filterUI.setOperation(filterUIState.getSelectedOperation().getDisplayValue());
    filtersById.put(filterUIState.getId(), filterUI);
    // // TODO special handling when putting into groupUI?
    groupUI.addToFilterGroup(filterUI);
    renderFilter(filterUI, filterUIState.getFilter(), possibleValues);

  }

  private void renderFilter(DynamicFilterUI filterUI, DynamicFilter dynamicFilter,
      List<Value> possibleValues) {
    // TODO honor dynamicFilter.getOperation()
    // TODO get strings from static finals

    String filterView = dynamicFilter.getOperation().getFilterView();
    if ("filterop.txt.eq".equals(filterView)) {
      DynamicFilterOperationOneFieldUI operationUI =
          new DynamicFilterOperationOneFieldUI(dynamicFilter.getMetaName());
      filterUI.addOperationUI(operationUI);
    } else if ("filterop.date.eq".equals(filterView)) {
      DynamicFilterOperationDateTimeInterval operationUI =
          new DynamicFilterOperationDateTimeInterval(dynamicFilter.getMetaName());
      filterUI.addOperationUI(operationUI);
      ArrayList<String> possibleOperations = new ArrayList<>();
      possibleOperations.add("Intervallum");
      possibleOperations.add("Időszakok");
      filterUI.setPossibleOperations(possibleOperations);
    } else if ("filterop.multi.eq".equals(filterView)) {
      DynamicFilterOperationMultiSelectUI operationUI =
          new DynamicFilterOperationMultiSelectUI(dynamicFilter.getMetaName());
      filterUI.addOperationUI(operationUI);
      operationUI.setItems(possibleValues);
    } else if ("filterop.combo.eq".equals(filterView)) {
      DynamicFilterOperationComboBoxUI operationUI =
          new DynamicFilterOperationComboBoxUI(dynamicFilter.getMetaName());
      filterUI.addOperationUI(operationUI);
      operationUI.setItems(possibleValues);
    }

  }

  @Override
  public void renderGroup(FilterGroupUIState groupUIState) {
    DynamicFilterGroupUI parentGroupUI;
    if (groupUIState.isRoot()) {
      parentGroupUI = null;
    } else {
      parentGroupUI = groupsById.get(groupUIState.getParentGroupId());
      if (parentGroupUI == null) {
        renderGroup(groupUIState.getParentGroup());
        parentGroupUI = groupsById.get(groupUIState.getParentGroupId());
      }
    }
    DynamicFilterGroupUI groupUI = new DynamicFilterGroupUI(groupUIState, parentGroupUI);
    groupsById.put(groupUIState.getId(), groupUI);
    if (parentGroupUI == null) {
      filterHolder.add(groupUI);
    } else {
      parentGroupUI.add(groupUI);
    }
  }

  @Override
  public void removeFilter(String filterId) {
    DynamicFilterUI filter = filtersById.get(filterId);
    filter.getGroup().getFiltersLayout().remove(filter);
  }

  @Override
  public void removeGroup(String groupId) {
    DynamicFilterGroupUI groupUI = groupsById.get(groupId);
    groupsById.remove(groupId);
    if (groupUI.getParentGroupUI() != null) {
      groupUI.getParentGroupUI().remove(groupUI);
    } else {
      throw new RuntimeException("Trying to remove root group UI!");
    }
  }

}
