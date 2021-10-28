package org.smartbit4all.ui.vaadin.components.userselector;

import org.smartbit4all.ui.api.userselector.UserSingleSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class UserSingleSelectorDialog extends UserSelectorDialog {

  private UserSingleSelectorViewModel userSelectorVM;

  public UserSingleSelectorDialog(String header, UserSingleSelectorViewModel userSelectorVM) {
    super(header);
    this.userSelectorVM = userSelectorVM;
    createAndBindUI(SelectionMode.SINGLE);
  }

  @Override
  protected void createAndBindUI(SelectionMode selectionMode) {
    super.createAndBindUI(selectionMode);

    VaadinBinders.bindItems(grid, userSelectorVM.userSingleSelector(), "selectors");
    VaadinBinders.bindSelection(grid.asSingleSelect(), userSelectorVM.userSingleSelector(), null,
        "selected");
  }
}
