package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilter;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroup;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.DynamicFilterView;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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
    VerticalLayout descriptorsLayout = new VerticalLayout();
    descriptorsLayout.addClassName("descriptors-layout");
    for (DynamicFilterDescriptor descriptor : filterConfig) {
      Label label = new Label();
      label.setText(label.getTranslation(descriptor.getName()));
      label.addClassName("descriptor-name");
      Button button = new Button(new Icon(VaadinIcon.PLUS));
      button.addClickListener(addFilterListener());
      FlexLayout flexLayout = new FlexLayout(label, button);
      descriptorsLayout.add(flexLayout);
    }
    
    descriptorHolder.add(descriptorsLayout);
//    groupHolder.add(new Label("Here comes the filter group root"));
  }

  private ComponentEventListener<ClickEvent<Button>> addFilterListener() {
    return e -> {
      UIUtils.showNotification("Button Pressed.");
    };
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
