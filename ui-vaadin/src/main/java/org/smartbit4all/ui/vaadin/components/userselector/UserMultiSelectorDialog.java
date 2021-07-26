package org.smartbit4all.ui.vaadin.components.userselector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.ui.api.userselector.UserMultiSelectorViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.binder.VaadinCollectionBinder;
import org.smartbit4all.ui.vaadin.object.VaadinPublisherWrapper;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.data.provider.ListDataProvider;

public class UserMultiSelectorDialog extends UserSelectorDialog {

  private UserMultiSelectorViewModel userSelectorVM;

  private List<UserSelector> list = new ArrayList<>();

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

    userSelectorVM.userMultiSelector().onCollectionObjectChange(null, "selected",
        selectedChanged -> {

          List<UserSelector> selectedUsers = new ArrayList<>();
          List<UserSelector> deletedUsers = new ArrayList<>();
          for (ObjectChangeSimple change : selectedChanged.getChanges()) {
            UserSelector selector = (UserSelector) change.getObject();
            if (change.getOperation().equals(ChangeState.NEW)) {
              selectedUsers.add(selector);
            } else if (change.getOperation().equals(ChangeState.DELETED)) {
              deletedUsers.add(selector);
            }
          }
          deletedUsers.removeAll(selectedUsers);
          for (UserSelector userSelector : deletedUsers) {
            if (grid.getSelectedItems().contains(userSelector)) {
              grid.deselect(userSelector);
            }
          }
          for (UserSelector userSelector : selectedUsers) {
            if (!grid.getSelectedItems().contains(userSelector)) {
              grid.select(userSelector);
            }
          }
        });
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
