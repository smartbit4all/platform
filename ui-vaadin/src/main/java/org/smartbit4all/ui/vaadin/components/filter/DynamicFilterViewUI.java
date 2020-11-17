package org.smartbit4all.ui.vaadin.components.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterConfig;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterView;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DynamicFilterViewUI implements DynamicFilterView {

  private DynamicFilterController controller;
  private HasComponents descriptorHolder;
  private HasComponents groupHolder;

  private Map<String, DynamicFilterGroupUI> groupsById = new HashMap<>();
  private Map<String, DynamicFilterUI> filtersById = new HashMap<>();
  private String activeGroupId;
  private Map<String, Details> detailsByGroupName;

  public DynamicFilterViewUI(DynamicFilterController controller, HasComponents descriptorHolder,
      HasComponents groupHolder) {
    this.controller = controller;
    this.descriptorHolder = descriptorHolder;
    this.groupHolder = groupHolder;
    this.activeGroupId = DynamicFilterController.ROOT_FILTER_GROUP;
    layoutComponents();
    controller.setUi(this);
    init();
  }

  private void init() {
    controller.loadData();
  }

  private void layoutComponents() {
    DynamicFilterGroupUI rootGroupUI = new DynamicFilterGroupUI();
    groupsById.put(DynamicFilterController.ROOT_FILTER_GROUP, rootGroupUI);
    groupHolder.add(rootGroupUI);
  }

  @Override
  public void renderFilterConfig(DynamicFilterConfig filterConfig) {
    descriptorHolder.add(createFilterDescriptorsLayout(filterConfig));
  }

  private VerticalLayout createFilterDescriptorsLayout(DynamicFilterConfig filterConfig) {
    VerticalLayout descriptorsLayout = new VerticalLayout();
    descriptorsLayout.addClassName("descriptors-layout");
    // TODO ability to add whole group to active filterPanel
    // TODO render dynamic filter descriptors inside groups

    detailsByGroupName = new HashMap<>();
    for (DynamicFilterGroupDescriptor filterDescriptor : filterConfig
        .getDynamicFilterGroupDescriptors()) {
      if (!detailsByGroupName.containsKey(filterDescriptor.getName())) {
        Details details = createDescriptorLayout(filterDescriptor);
        descriptorsLayout.add(details);
        detailsByGroupName.put(filterDescriptor.getName(), details);
      }
    }

    for (DynamicFilterDescriptor descriptor : filterConfig.getDynamicFilterDescriptors()) {
      if (detailsByGroupName.containsKey(descriptor.getGroupName())) {
        detailsByGroupName.get(descriptor.getGroupName())
            .addContent(createDescriptorUI(descriptor));
      }
    }

    return descriptorsLayout;
  }

  private Details createDescriptorLayout(DynamicFilterGroupDescriptor filterDescriptor) {
    Details details = new Details();
    Label summaryText = new Label(details.getTranslation(filterDescriptor.getName()));
    Button btnChooseGroup = new Button(new Icon(VaadinIcon.PLUS));
    btnChooseGroup.addClickListener(groupSelectButtonListener());

    FlexLayout summary = new FlexLayout(summaryText, btnChooseGroup);

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

  private Component createDescriptorUI(DynamicFilterDescriptor descriptor) {
    Label label = new Label();
    label.setText(label.getTranslation(descriptor.getName()));
    label.addClassName("descriptor-name");
    Button button = new Button(new Icon(VaadinIcon.PLUS));
    button.addClickListener(addFilterListener(descriptor.getGroupName(), descriptor.getName()));
    FlexLayout descriptorUI = new FlexLayout(label, button);
    descriptorUI.addClassName("descriptor-layout");
    return descriptorUI;
  }

  private ComponentEventListener<ClickEvent<Button>> addFilterListener(String groupName,
      String descriptorName) {
    return e -> {
      controller.addFilter(groupName, descriptorName);
    };
  }

  @Override
  public void renderFilter(String groupId, String filterId, DynamicFilter dynamicFilter,
      List<DynamicFilterOperation> filterOperations) {
    DynamicFilterGroupUI groupUI = groupsById.get(groupId);
    if (groupUI == null) {
      throw new IllegalArgumentException("No groupUI found with '" + groupId + "' groupId!");
    }
    DynamicFilterUI filterUI = filtersById.get(filterId);
    if (filterUI != null) {
      throw new IllegalArgumentException(
          "Existing filterUI found when creating new filterUI with '" + filterId + "' filterId!");
    }
    filterUI = new DynamicFilterUI(groupUI);
    filterUI.getButton().addClickListener(e -> controller.removeFilter(filterId));
    filtersById.put(filterId, filterUI);
    // TODO special handling when putting into groupUI?
    groupUI.add(filterUI);
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
        new DynamicFilterOperationOneFieldUI(dynamicFilter.getDescriptorName());
    filterUI.addOperation(operationUI);
  }

  @Override
  public void renderGroup(String parentGroupId, String childGroupId,
      DynamicFilterGroup childGroup) {

    DynamicFilterGroupUI parentGroupUI = groupsById.get(parentGroupId);
    if (parentGroupUI == null) {
      throw new IllegalArgumentException(
          "No parentGroupUI found with '" + parentGroupId + "' groupId!");
    }
    DynamicFilterGroupUI childGroupUI = new DynamicFilterGroupUI();
    groupsById.put(childGroupId, childGroupUI);
    // TODO set groupUI properties according to childGroup
    parentGroupUI.add(childGroupUI);

  }

  @Override
  public void removeFilter(String filterId) {
    DynamicFilterUI filter = filtersById.get(filterId);
    filter.getGroup().remove(filter);
  }


}
