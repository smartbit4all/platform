package org.smartbit4all.ui.vaadin.components.storage.history;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.navigation.UIViewParameterVaadinTransition;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;

public class ObjectHistoryDialog extends Dialog {

  private ObjectHistoryViewModel objectHistoryVM;
  private String viewName;
  private Map<String, Object> objectParams;

  private Grid<ObjectHistoryEntry> historyGrid;
  private Button close;

  public ObjectHistoryDialog(ObjectHistoryViewModel objectHistoryVM, String viewName,
      Map<String, Object> objectParams) {
    this.objectHistoryVM = objectHistoryVM;
    this.viewName = viewName;
    this.objectParams = objectParams;

    URI objectUri = (URI) objectParams.get("entry");

    createUI();
    objectHistoryVM.setObjectHistoryRef(objectUri, objectUri.getScheme());
  }

  private void createUI() {
    setHeight("60%");
    setWidth("60%");
    setResizable(true);

    historyGrid = new Grid<>();
    historyGrid.addColumn(ObjectHistoryEntry::getSummary).setHeader("Verzió");
    historyGrid.addComponentColumn(h -> openHistoryButton(h));

    VaadinBinders.bind(historyGrid, objectHistoryVM.objectHistory(), null,
        ObjectHistory.OBJECT_HISTORY_ENTRIES);

    close = new Button("Bezárás", e -> close());
    close.getStyle().set("cursor", "pointer");

    add(historyGrid, close);
  }

  private Button openHistoryButton(ObjectHistoryEntry historyEntry) {
    Map<String, Object> versionParams = new HashMap<>(objectParams);

    versionParams.put("entry", historyEntry.getVersionUri());

    UIViewParameterVaadinTransition versionParam =
        new UIViewParameterVaadinTransition(versionParams);

    Button open = new Button("Megnyitás",
        e -> {
          getUI().ifPresent(ui -> ui.navigate(viewName, versionParam.construct()));
          close();
        });
    open.getStyle().set("cursor", "pointer");
    return open;
  }
}
