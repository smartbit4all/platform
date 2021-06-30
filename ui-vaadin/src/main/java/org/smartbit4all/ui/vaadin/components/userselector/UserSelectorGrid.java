package org.smartbit4all.ui.vaadin.components.userselector;


import org.smartbit4all.api.userselector.bean.UserSelector;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class UserSelectorGrid extends Grid<UserSelector> {
  
  public UserSelectorGrid(String header) {
    super(UserSelector.class, false);
    addThemeName("grid-selection-theme");
    addThemeName("no-row-borders");
    addThemeName("no-border");
    addClassName("org-selector-grid");

    addComponentColumn(this::createColumn).setHeader(header);
  }
  
  private Component createColumn(UserSelector userSelector) {
    HorizontalLayout layout = new HorizontalLayout();
    Icon icon = new Icon(VaadinIcon.valueOf(userSelector.getKind().getValue()));
    Label label = new Label(userSelector.getDisplayName());
    
    layout.add(icon, label);
    
    return layout;
  }
}
