package org.smartbit4all.ui.vaadin.components.userselector;

import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class UserMultiSelectorDialogNew extends UserSelectorDialogNew {

  public UserMultiSelectorDialogNew(UserSelectorViewModel userSelectorVM, String header) {
    super(userSelectorVM, header);
    createAndBindUI(SelectionMode.MULTI);
  }

  @Override
  protected void createAndBindUI(SelectionMode selectionMode) {
    super.createAndBindUI(selectionMode);

    VaadinBinders.bindItems(grid, userSelectorVM.multiSelector(), "selectors");
    VaadinBinders.bindSelection(grid.asMultiSelect(), userSelectorVM.multiSelector(), "selected");
  }
}
