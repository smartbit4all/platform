package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.vaadin.util.IconSize;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DynamicFilterGroupUI extends FlexLayout {


  private DynamicFilterGroupUI parentGroupUI;
  private FlexLayout iconLayout;

  public DynamicFilterGroupUI(boolean isRoot, DynamicFilterGroupUI parentGroupUI,
      boolean isClosable) {
    // TODO maybe we can skip isRoot parameter and use: isRoot = parentGroupUI == null;
    this.parentGroupUI = parentGroupUI;
    iconLayout = new FlexLayout();
    iconLayout.addClassName("icon-layout");
    add(iconLayout);

    if (isRoot) {
      addClassName("dynamic-filtergroup-root");
    } else {
      addClassName("dynamic-filtergroup");
    }

    // TODO handle isCloseable, add close button if true.
  }

  public DynamicFilterGroupUI getParentGroupId() {
    return parentGroupUI;
  }
  
  public void setIconLayout(String icon, String label) {
    iconLayout.removeAll();
    VaadinIcon vaadinIcon = VaadinIcon.valueOf(icon.toUpperCase());
    Icon filterIcon = UIUtils.createIcon(IconSize.S, TextColor.TERTIARY, vaadinIcon);
    filterIcon.addClassName("filter-icon");
    Label filterLabel = UIUtils.createH4Label(getTranslation(label));
    filterLabel.addClassName("filter-label");
    iconLayout.add(filterIcon, filterLabel);
  }
}
