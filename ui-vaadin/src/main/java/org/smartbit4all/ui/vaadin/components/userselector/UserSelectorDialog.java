package org.smartbit4all.ui.vaadin.components.userselector;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class UserSelectorDialog extends Dialog {
  
  private VerticalLayout layout;
  private Button close;
  
  private String header;
  protected UserSelectorGrid grid;
  
  public UserSelectorDialog(String header) {
    this.header = header;
  }
  
  protected void createAndBindUI(SelectionMode selectionMode) {
    setWidth("40%");
    
    layout = new VerticalLayout();
    grid = new UserSelectorGrid(header, selectionMode);
    close = new Button("Bezárás", e -> close());
    
    layout.add(grid, close);
    layout.setHorizontalComponentAlignment(Alignment.CENTER, close);
    
    add(layout);
  }
}
