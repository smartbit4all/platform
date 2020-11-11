package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterView;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Label;

public class DynamicFilterViewUI implements DynamicFilterView {

  private DynamicFilterController controller;
  private HasComponents descriptorHolder;
  private HasComponents groupHolder;
  
  public DynamicFilterViewUI(DynamicFilterController controller, HasComponents descriptorHolder, HasComponents groupHolder) {
    this.controller = controller;
    this.descriptorHolder = descriptorHolder;
    this.groupHolder = groupHolder;
    init();
    controller.setUi(this);
  }

  private void init() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void renderFilterConfig(List<DynamicFilterDescriptor> filterConfig) {
    // TODO Auto-generated method stub
    descriptorHolder.add(new Label("Here comes the list of descriptors"));
    groupHolder.add(new Label("Here comes the filter group root"));
  }

  @Override
  public void renderFilter(String groupId, String filterId, DynamicFilter dynamicFilter,
      List<DynamicFilterOperation> filterOptions) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void renderFilter(String filterId, DynamicFilter dynamicFilter) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void renderGroup(String parentGroupId, String childGroupId,
      DynamicFilterGroup childGroup) {
    // TODO Auto-generated method stub
    
  }
  
}
