package org.smartbit4all.ui.vaadin.components.userselector;

import java.io.Closeable;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;

public class UserSelectorDialogNew extends Dialog {

  protected UserSelectorViewModel userSelectorVM;

  protected FlexLayout layout;
  protected FlexLayout buttonLayout;
  protected Button close;
  protected Button save;

  private String header;
  protected UserSelectorGrid grid;

  public UserSelectorDialogNew(UserSelectorViewModel userSelectorVM, String header) {
    this.userSelectorVM = userSelectorVM;
    this.header = header;
  }

  protected void createAndBindUI(SelectionMode selectionMode) {
    setWidth("40%");

    layout = new FlexLayout();
    layout.setFlexDirection(FlexDirection.COLUMN);
    grid = new UserSelectorGrid(header, selectionMode);

    save = new Button("Mentés", e -> saveAndCloseDialog());
    close = new Button("Bezárás", e -> closeDialog());
    buttonLayout = new FlexLayout(save, close);
    buttonLayout.setFlexDirection(FlexDirection.ROW);

    layout.add(grid, buttonLayout);

    add(layout);
  }


  protected void saveAndCloseDialog() {
    List<String> urisAsText = getUserUriStream().collect(Collectors.toList());
    userSelectorVM.executeCommand(UserSelectorViewModel.SAVE_CMD,
        urisAsText.toArray(new String[0]));
    close();
  }

//  protected void saveOnClose() {
//    List<String> urisAsText = getUserUriStream().collect(Collectors.toList());
//    userSelectorVM.executeCommand(UserSelectorViewModel.SAVE_CLOSE_CMD,
//        urisAsText.toArray(new String[0]));
//    close();
//  }

  protected Stream<String> getUserUriStream() {
    return grid.getSelectedItems().stream().map(UserSelector::getUri).map(URI::toString);
  }

  protected void closeDialog() {
    userSelectorVM.executeCommand(UserSelectorViewModel.CLOSE_CMD);
    close();
  }
}
