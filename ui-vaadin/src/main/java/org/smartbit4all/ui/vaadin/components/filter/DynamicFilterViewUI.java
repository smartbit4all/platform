package org.smartbit4all.ui.vaadin.components.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterMeta;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterLabelPosition;
import org.smartbit4all.ui.common.filter.DynamicFilterView;
import org.smartbit4all.ui.vaadin.util.UIUtils;
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
  private String activeGroupId;
  private Map<String, Details> detailsByGroupName;

  public DynamicFilterViewUI(DynamicFilterController controller, HasComponents filterSelectorHolder,
      HasComponents filterHolder) {
    this.controller = controller;
    this.filterSelectorHolder = filterSelectorHolder;
    this.filterHolder = filterHolder;
    this.activeGroupId = DynamicFilterController.ROOT_FILTER_GROUP;
    layoutComponents();
    controller.setUi(this);
  }

  @Override
  public void init() {
    controller.loadData();
  }

  private void layoutComponents() {
    DynamicFilterGroupUI rootGroupUI = new DynamicFilterGroupUI(true, null, false);
    groupsById.put(DynamicFilterController.ROOT_FILTER_GROUP, rootGroupUI);
    filterHolder.add(rootGroupUI);
  }

  @Override
  public void renderFilterSelectors(DynamicFilterConfig filterConfig) {
    filterSelectorHolder.add(createFilterSelectorLayout(filterConfig));
  }

  private VerticalLayout createFilterSelectorLayout(DynamicFilterConfig filterConfig) {
    VerticalLayout filterSelectorLayout = new VerticalLayout();
    filterSelectorLayout.addClassName("filterselector-layout");
    // metaLayout.addClassName("descriptors-layout");
    // TODO ability to add whole group to active filterPanel

    detailsByGroupName = new HashMap<>();
    for (DynamicFilterGroupMeta groupMeta : filterConfig
        .getDynamicFilterGroupMetas()) {
      if (!detailsByGroupName.containsKey(groupMeta.getName())) {
        Details details = createGroupMetaLayout(groupMeta);
        filterSelectorLayout.add(details);
        detailsByGroupName.put(groupMeta.getName(), details);
      }
    }

    for (DynamicFilterMeta filterMeta : filterConfig.getDynamicFilterMetas()) {
      if (detailsByGroupName.containsKey(filterMeta.getGroupName())) {
        detailsByGroupName.get(filterMeta.getGroupName())
            .addContent(createFilterMetaUI(filterMeta));
      }
    }

    return filterSelectorLayout;
  }

  private Details createGroupMetaLayout(DynamicFilterGroupMeta groupMeta) {
    Details details = new Details();
    details.addThemeVariants(DetailsVariant.FILLED);
    Label summaryText = new Label(details.getTranslation(groupMeta.getName()));
    // Button btnChooseGroup = new Button(new Icon(VaadinIcon.PLUS));
    // btnChooseGroup.addClickListener(groupSelectButtonListener());

    FlexLayout summary = new FlexLayout(summaryText);

    details.setSummary(summary);
    details.setOpened(true);
    return details;
  }

  private ComponentEventListener<ClickEvent<Button>> groupSelectButtonListener() {
    return e -> {
      UIUtils.showNotification("Megnyomva");
    };
  }



  private ComponentEventListener<ClickEvent<Icon>> groupSelectListener() {
    // TODO Auto-generated method stub
    return null;
  }

  private Component createFilterMetaUI(DynamicFilterMeta filterMeta) {
    Label label = new Label();
    label.setText(label.getTranslation(filterMeta.getName()));
    // label.addClassName("filtermeta-name");
    // label.addClassName("descriptor-name");
    Button button = new Button(new Icon(VaadinIcon.PLUS));
    // button.addClassName("plus-button");
    button.addClickListener(addFilterListener(filterMeta.getGroupName(), filterMeta.getName()));
    FlexLayout filterMetaUI = new FlexLayout(label, button);
    filterMetaUI.addClassName("filtermeta-layout");
    // filterMetaUI.addClassName("descriptor-layout");
    return filterMetaUI;
  }

  private ComponentEventListener<ClickEvent<Button>> addFilterListener(String groupName,
      String filterMetaName) {
    return e -> {
      controller.addFilter(groupName, filterMetaName, true, DynamicFilterLabelPosition.ON_LEFT);
    };
  }

  @Override
  public void renderFilter(String groupId, String filterId, DynamicFilter dynamicFilter,
      List<DynamicFilterOperation> filterOperations, boolean isClosable, DynamicFilterLabelPosition position) {
    DynamicFilterGroupUI groupUI = groupsById.get(groupId);
    if (groupUI == null) {
      throw new IllegalArgumentException("No groupUI found with '" + groupId + "' groupId!");
    }
    DynamicFilterUI filterUI = filtersById.get(filterId);
    if (filterUI != null) {
      throw new IllegalArgumentException(
          "Existing filterUI found when creating new filterUI with '" + filterId + "' filterId!");
    }
    filterUI = new DynamicFilterUI(groupUI, isClosable, position);
    filterUI.setLabel(filterOperations.get(0).getDisplayValue());
    if (isClosable) {
      filterUI.getCloseButton()
          .addClickListener(e -> controller.removeFilter(groupId, filterId));
    }
    filtersById.put(filterId, filterUI);
    // TODO special handling when putting into groupUI?
    groupUI.addToFilterGroup(filterUI);
    renderFilter(filterUI, dynamicFilter);
  }

  @Override
  public void renderFilter(String filterId, DynamicFilter dynamicFilter) {
    DynamicFilterUI filterUI = filtersById.get(filterId);
    if (filterUI == null) {
      throw new IllegalArgumentException("No filterUI found with '" + filterId + "' filterId!");
    }
    renderFilter(filterUI, dynamicFilter);
  }


  private void renderFilter(DynamicFilterUI filterUI, DynamicFilter dynamicFilter) {
    // TODO honor dynamicFilter.getOperation()
    DynamicFilterOperationOneFieldUI operationUI =
        new DynamicFilterOperationOneFieldUI(dynamicFilter.getMetaName());
    filterUI.addOperation(operationUI);
  }

  @Override
  public void renderGroup(String parentGroupId, String groupId, DynamicFilterGroup group,
      boolean isClosable) {

    DynamicFilterGroupUI parentGroupUI = groupsById.get(parentGroupId);
    if (parentGroupUI == null) {
      throw new IllegalArgumentException(
          "No parentGroupUI found with '" + parentGroupId + "' groupId!");
    }
    DynamicFilterGroupUI groupUI = new DynamicFilterGroupUI(false, parentGroupUI, isClosable);
    groupUI.setIconLayout(group.getIcon(), group.getName());
    groupsById.put(groupId, groupUI);
    // TODO set groupUI properties according to group
    parentGroupUI.add(groupUI);

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
