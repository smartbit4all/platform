package org.smartbit4all.ui.vaadin.components.storage.history;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.ui.api.data.storage.history.ObjectHistoryViewModel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;

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
    setHeight("70%");
    setWidth("70%");
    setResizable(true);

    FlexLayout mainLayout = new FlexLayout();
    mainLayout.setWidthFull();
    mainLayout.setHeightFull();
    mainLayout.setFlexDirection(FlexDirection.COLUMN);

    historyGrid = new Grid<>();
    historyGrid.setHeight("85%");
    historyGrid.setItemDetailsRenderer(
        new ComponentRenderer<>(entry -> getHistoryEntryChangesLayout(entry)));

    Column<ObjectHistoryEntry> summary =
        historyGrid.addColumn(ObjectHistoryEntry::getSummary).setHeader("Verzió");
    summary.setAutoWidth(true);

    Column<ObjectHistoryEntry> createdBy =
        historyGrid.addColumn(item -> item.getVersion().getCreatedBy()).setHeader("Módosító");
    createdBy.setAutoWidth(true);

    Column<ObjectHistoryEntry> createdAt =
        historyGrid.addColumn(item -> getFormattedCreatedAt(item)).setHeader("Időpont");
    createdAt.setAutoWidth(true);

    // historyGrid.addColumn(item -> item.getVersion().getOperation).setHeader("");

    historyGrid.addComponentColumn(h -> openHistoryButton(h));

    historyGrid.sort(Collections.singletonList(
        new GridSortOrder<>(createdAt, SortDirection.DESCENDING)));

    VaadinBinders.bindItems(historyGrid, objectHistoryVM.objectHistory(),
        ObjectHistory.OBJECT_HISTORY_ENTRIES);

    close = new Button("Bezárás", e -> close());
    close.setWidth("120px");
    close.getStyle().set("cursor", "pointer");

    FlexLayout closeLayout = new FlexLayout(close);
    closeLayout.setFlexDirection(FlexDirection.COLUMN);
    closeLayout.setAlignItems(Alignment.CENTER);
    historyGrid.setHeight("10%");
    closeLayout.getStyle().set("margin-top", "10px");

    mainLayout.add(historyGrid, closeLayout);
    add(mainLayout);
  }

  private FlexLayout getHistoryEntryChangesLayout(ObjectHistoryEntry entry) {
    FlexLayout layout = new FlexLayout();
    layout.setWidthFull();

    TextArea textArea = new TextArea();
    textArea.setReadOnly(true);
    textArea.setWidthFull();

    if (entry.getChanges() == null) {
      textArea.setValue("Létrehozáskor keletkezett verzió.");
    } else {
      textArea.setValue(entry.getChanges());
    }

    layout.add(textArea);
    return layout;
  }

  private Object getFormattedCreatedAt(ObjectHistoryEntry item) {
    OffsetDateTime createdAt = item.getVersion().getCreatedAt();
    return createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
  }

  private Button openHistoryButton(ObjectHistoryEntry historyEntry) {
    Button open = new Button("Megnyitás",
        e -> {
          objectHistoryVM.executeCommand(null,
              ObjectHistoryViewModel.OPEN_VERSION,
              historyEntry.getVersionUri(),
              viewName);
        });
    open.getStyle().set("cursor", "pointer");
    return open;
  }
}
