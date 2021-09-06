package org.smartbit4all.ui.vaadin.components.userselector;

import java.net.URI;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class UserSingleSelectorDialogNew extends UserSelectorDialogNew {

  public UserSingleSelectorDialogNew(UserSelectorViewModel userSelectorVM, String header) {
    super(userSelectorVM, header);
    createAndBindUI(SelectionMode.SINGLE);
  }

  @Override
  protected void createAndBindUI(SelectionMode selectionMode) {
    super.createAndBindUI(selectionMode);

    VaadinBinders.bind(grid, userSelectorVM.singleSelector(), null, "selectors");
    VaadinBinders.bindSelection(grid.asSingleSelect(), userSelectorVM.singleSelector(), null,
        "selected");
  }
  
  @Override
  protected void saveAndCloseDialog() {
    String selectedUri = grid.getSelectedItems().stream()
        .map(UserSelector::getUri)
        .map(URI::toString)
        .findFirst()
        .orElse("");
    userSelectorVM.executeCommand(UserSelectorViewModel.SAVE_CMD, selectedUri);
    close();
  }
}