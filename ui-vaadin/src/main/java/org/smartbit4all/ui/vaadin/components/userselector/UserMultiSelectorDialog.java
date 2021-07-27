package org.smartbit4all.ui.vaadin.components.userselector;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.ui.api.userselector.UserMultiSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class UserMultiSelectorDialog extends UserSelectorDialog {

  private UserMultiSelectorViewModel userSelectorVM;

  public UserMultiSelectorDialog(String header, UserMultiSelectorViewModel userSelectorVM) {
    super(header);
    this.userSelectorVM = userSelectorVM;
    createAndBindUI(SelectionMode.MULTI);
    addDialogCloseActionListener(closeEvent -> closeDialog());
    save.addClickListener(saveEvent -> saveDialog());
    userSelectorVM.init();
  }

  @Override
  protected void createAndBindUI(SelectionMode selectionMode) {
    super.createAndBindUI(selectionMode);

    VaadinBinders.bind(grid, userSelectorVM.userMultiSelector(), null, "selectors");
    VaadinBinders.bindSelection(grid.asMultiSelect(), userSelectorVM.userMultiSelector(), null,
        "selected");
  }

  private void closeDialog() {
    try {
      userSelectorVM.executeCommand(UserMultiSelectorViewModel.CLOSE_CMD);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void saveDialog() {
    try {
      List<String> urisAsText = grid.getSelectedItems().stream().map(UserSelector::getUri)
          .map(URI::toString).collect(Collectors.toList());
      userSelectorVM.executeCommand(UserMultiSelectorViewModel.SAVE_CMD,
          urisAsText.toArray(new String[0]));
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
