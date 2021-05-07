package org.smartbit4all.ui.vaadin.components.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.object.ChangeState;
import org.smartbit4all.api.object.CollectionObjectChange;
import org.smartbit4all.api.object.ObjectChangeSimple;
import org.smartbit4all.api.object.ObjectEditing;
import com.vaadin.flow.component.grid.Grid;

public class VaadinGridBinder<T, E extends ObjectEditing> {

  private Grid<T> grid;
  private E editing;

  private List<T> items;
  private Map<String, T> itemsByPath;

  public VaadinGridBinder(Grid<T> grid, E editing, String path, String collectionName) {
    this.grid = grid;
    this.editing = editing;
    this.items = new ArrayList<>();
    this.itemsByPath = new HashMap<>();
    this.editing.publisher().collectionObjects().subscribe().collection(path, collectionName)
        .add(this::onCollectionObjectChanged);
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
