package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterView;

public class DynamicFilterViewUI implements DynamicFilterView {

  private DynamicFilterController controller;
  
  public DynamicFilterViewUI(DynamicFilterController controller) {
    this.controller = controller;
    init();
    controller.setUi(this);
  }

  private void init() {
    // TODO Auto-generated method stub
    
  }
  
}
