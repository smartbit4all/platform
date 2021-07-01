package org.smartbit4all.ui.vaadin.components.userselector;

import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class UserSelectorDialog extends Dialog {
  
  private VerticalLayout layout;
  private UserSelectorViewModel userSelectorVM;
  private Button close;
  
  private String header;
  private UserSelectorGrid grid;
  
  public UserSelectorDialog(String header, UserSelectorViewModel userSelectorVM) {
    this.header = header;
    this.userSelectorVM = userSelectorVM;
    
    createAndBindUI();
  }
  
  private void createAndBindUI() {
    setWidth("40%");
    
    layout = new VerticalLayout();
    grid = new UserSelectorGrid(header);
    close = new Button("Bezárás", e -> close());
    
    VaadinBinders.bind(grid, userSelectorVM.userSelectors(), null, "selectors");
    VaadinBinders.bindSelection(grid.asSingleSelect(), userSelectorVM.userSelectors(), null, "selected");
    
    layout.add(grid, close);
    layout.setHorizontalComponentAlignment(Alignment.CENTER, close);
    
    add(layout);
  }
}
