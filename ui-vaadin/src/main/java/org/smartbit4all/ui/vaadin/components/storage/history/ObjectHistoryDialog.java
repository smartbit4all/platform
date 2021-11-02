package org.smartbit4all.ui.vaadin.components.storage.history;

import java.net.URI;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;

public class ObjectHistoryDialog extends Dialog {

  private ObjectHistoryViewModel objectHistoryVM;
  private String viewName;

  private Grid<ObjectHistoryEntry> historyGrid;
  private Button close;

  public ObjectHistoryDialog(ObjectHistoryViewModel objectHistoryVM, URI objectUri,
      String viewName) {
    this.objectHistoryVM = objectHistoryVM;
    this.viewName = viewName;

    createUI();
    objectHistoryVM.executeCommand(null,
        ObjectHistoryViewModel.SET_OBJECT,
        objectUri,
        objectUri.getScheme());
  }

  private void createUI() {
    setHeight("60%");
    setWidth("60%");
    setResizable(true);

    historyGrid = new Grid<>();
    historyGrid.addColumn(ObjectHistoryEntry::getSummary).setHeader("Verzió");
    historyGrid.addComponentColumn(h -> openHistoryButton(h));

    VaadinBinders.bindItems(historyGrid, objectHistoryVM.objectHistory(),
        ObjectHistory.OBJECT_HISTORY_ENTRIES);

    close = new Button("Bezárás", e -> close());
    close.getStyle().set("cursor", "pointer");

    add(historyGrid, close);
  }

  private Button openHistoryButton(ObjectHistoryEntry historyEntry) {
    Button open = new Button("Megnyitás",
        e -> {
//          objectHistoryVM.executeCommand(null,
//              ObjectHistoryViewModel.OPEN_VERSION,
//              historyEntry.getVersionUri(),
//              viewName);
        });
    open.getStyle().set("cursor", "pointer");
    return open;
  }
}
