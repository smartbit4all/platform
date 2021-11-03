package org.smartbit4all.ui.vaadin.components.storage.history;

import java.util.List;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;

public class AllHistoryDialog extends Dialog {

  private ObjectHistoryViewModel objectHistoryVM;

  private Grid<ObjectHistoryEntry> historyGrid;
  private Button close;
  
  public AllHistoryDialog(ObjectHistoryViewModel objectHistoryVM, List<ObjectHistoryEntry> allHistory) {
    this.objectHistoryVM = objectHistoryVM;
    
    createUI();
    objectHistoryVM.setHistoryEntries(allHistory);
  }
  
  private void createUI() {
    setHeight("60%");
    setWidth("60%");
    setResizable(true);

    historyGrid = new Grid<>();
    historyGrid.addColumn(ObjectHistoryEntry::getSummary).setHeader("Verzió");

    VaadinBinders.bindItems(historyGrid, objectHistoryVM.objectHistory(),
        ObjectHistory.OBJECT_HISTORY_ENTRIES);

    close = new Button("Bezárás", e -> close());
    close.getStyle().set("cursor", "pointer");

    add(historyGrid, close);
  }
}
