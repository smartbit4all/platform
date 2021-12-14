package org.smartbit4all.ui.vaadin.components.userselector;

import org.smartbit4all.api.userselector.bean.UserMultiSelector;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class UserMultiSelectorDialog extends UserSelectorDialog {

  public UserMultiSelectorDialog(UserSelectorViewModel userSelectorVM, String header) {
    super(userSelectorVM, header);
    createAndBindUI(SelectionMode.MULTI);
  }

  @Override
  protected void createAndBindUI(SelectionMode selectionMode) {
    super.createAndBindUI(selectionMode);

    VaadinBinders.bindItems(grid, userSelectorVM.multiSelector(),
        UserMultiSelector.SELECTORS);
    VaadinBinders.bindSelection(grid.asMultiSelect(), userSelectorVM.multiSelector(),
        UserMultiSelector.SELECTED);
  }
}
