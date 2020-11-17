package org.smartbit4all.ui.vaadin.components.filter;

import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DynamicFilterGroupUI extends FlexLayout {


  private DynamicFilterGroupUI parentGroupUI;

  public DynamicFilterGroupUI(boolean isRoot, DynamicFilterGroupUI parentGroupUI) {
    // TODO maybe we can skip isRoot parameter and use: isRoot = parentGroupUI == null;
    this.parentGroupUI = parentGroupUI;

    if (isRoot) {
      addClassName("dynamic-filtergroup-root");
    } else {
      addClassName("dynamic-filtergroup");
    }
  }

  public DynamicFilterGroupUI getParentGroupId() {
    return parentGroupUI;
  }
}
