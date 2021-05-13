package org.smartbit4all.ui.vaadin.components.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.grid.Grid;

public class VaadinGridBinder<T> {

  private Grid<T> grid;
  private ObservableObject observableObject;

  private List<T> items;
  private Map<String, T> itemsByPath;

  public VaadinGridBinder(Grid<T> grid, ObservableObject observableObject, String path,
      String collectionName) {
    this.grid = grid;
    this.observableObject = observableObject;
    this.items = new ArrayList<>();
    this.itemsByPath = new HashMap<>();
    this.observableObject.onCollectionObjectChange(path, collectionName,
        this::onCollectionObjectChanged);
    this.grid.setItems(items);
  }

  @SuppressWarnings("unchecked")
  protected void onCollectionObjectChanged(CollectionObjectChange changes) {
    for (ObjectChangeSimple change : changes.getChanges()) {
      String itemPath = change.getPath();
      if (change.getOperation().equals(ChangeState.NEW)) {
        T item = (T) change.getObject();
        items.add(item);
        itemsByPath.put(itemPath, item);
        grid.getDataProvider().refreshItem(item);
      } else if (change.getOperation().equals(ChangeState.DELETED)) {
        if (itemsByPath.containsKey(itemPath)) {
          T item = itemsByPath.get(itemPath);
          items.remove(item);
          grid.getDataProvider().refreshItem(item);
        }
      } else if (change.getOperation().equals(ChangeState.MODIFIED)) {
        T item = (T) change.getObject();
        grid.getDataProvider().refreshItem(item);
      }
    }

  }

}
