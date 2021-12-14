package org.smartbit4all.ui.vaadin.components.userselector;

import org.smartbit4all.api.userselector.bean.UserSingleSelector;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class UserSingleSelectorDialog extends UserSelectorDialog {

  public UserSingleSelectorDialog(UserSelectorViewModel userSelectorVM, String header) {
    super(userSelectorVM, header);
    createAndBindUI(SelectionMode.SINGLE);
  }

  @Override
  protected void createAndBindUI(SelectionMode selectionMode) {
    super.createAndBindUI(selectionMode);

    VaadinBinders.bindItems(grid, userSelectorVM.singleSelector(),
        UserSingleSelector.SELECTORS);
    VaadinBinders.bindSelection(grid.asSingleSelect(), userSelectorVM.singleSelector(), null,
        UserSingleSelector.SELECTED);
  }

  @Override
  protected void saveAndCloseDialog() {
    String selectedUri = getUserUriStream()
        .findFirst()
        .orElse("");
    userSelectorVM.executeCommand(UserSelectorViewModel.SAVE_CMD, selectedUri);
    close();
  }
}
