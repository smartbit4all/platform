package org.smartbit4all.ui.vaadin.components.userselector;

import org.smartbit4all.ui.api.userselector.UserMultiSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class UserMultiSelectorDialog extends UserSelectorDialog {
  
  private UserMultiSelectorViewModel userSelectorVM;

  public UserMultiSelectorDialog(String header, UserMultiSelectorViewModel userSelectorVM) {
    super(header);
    this.userSelectorVM = userSelectorVM;
    createAndBindUI(SelectionMode.MULTI);
  }

  @Override
  protected void createAndBindUI(SelectionMode selectionMode) {
    super.createAndBindUI(selectionMode);
    
    VaadinBinders.bind(grid, userSelectorVM.userMultiSelector(), null, "selectors");
    VaadinBinders.bindSelection(grid.asMultiSelect(), userSelectorVM.userMultiSelector(), null, "selected");
  }
}
