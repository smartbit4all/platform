package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOption;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterView;
import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import com.vaadin.flow.component.Composite;

public class DynamicFilterViewUI extends Composite<FlexBoxLayout> implements DynamicFilterView {

  private DynamicFilterController controller;
  
  public DynamicFilterViewUI(DynamicFilterController controller) {
    this.controller = controller;
    init();
    controller.setUi(this);
  }

  private void init() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void renderFilterConfig(List<DynamicFilterDescriptor> filterConfig) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void renderFilter(String groupId, String filterId, DynamicFilter dynamicFilter,
      List<DynamicFilterOption> filterOptions) {
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
