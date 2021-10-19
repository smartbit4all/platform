package org.smartbit4all.ui.vaadin.components.storage.history;

import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;

public class ObjectHistoryDialog extends Dialog {

  private ObjectHistoryViewModel objectHistoryVM;

  private Grid<ObjectHistoryEntry> historyGrid;
  private Button close;

  public <T extends ObjectVersionView> ObjectHistoryDialog(ObjectHistoryViewModel objectHistoryVM,
      Class<T> cls) {
    this.objectHistoryVM = objectHistoryVM;

    createUI(cls);
  }

  private <T extends ObjectVersionView> void createUI(Class<T> cls) {
    setHeight("60%");
    setWidth("60%");
    setResizable(true);

    historyGrid = new Grid<>();
    historyGrid.addColumn(ObjectHistoryEntry::getSummary).setHeader("Verzió");
    historyGrid.addComponentColumn(h -> {
      try {
        return openHistoryButton(h, cls);
      } catch (Exception e1) {
        throw new IllegalStateException();
      }
    });

    VaadinBinders.bind(historyGrid, objectHistoryVM.objectHistory(), null,
        ObjectHistory.OBJECT_HISTORY_ENTRIES);

    close = new Button("Bezárás", e -> close());
    close.getStyle().set("cursor", "pointer");

    add(historyGrid, close);
  }

  private <T extends ObjectVersionView> Button openHistoryButton(ObjectHistoryEntry historyEntry,
      Class<T> cls) throws Exception {
    T versionView = cls.newInstance();
    versionView.createUI(historyEntry.getVersionUri());

    ObjectHistoryDetailDialog historyDetails = new ObjectHistoryDetailDialog(versionView);
    Button open = new Button("Megnyitás",
        e -> historyDetails.open());
    open.getStyle().set("cursor", "pointer");
    return open;
  }
}
