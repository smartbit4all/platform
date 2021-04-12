package org.smartbit4all.ui.vaadin.components.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.smartbit4all.api.object.ChangeState;
import org.smartbit4all.api.object.CollectionChange;
import org.smartbit4all.api.object.ObjectChange;
import org.smartbit4all.api.object.ObjectEditing;
import com.vaadin.flow.component.grid.Grid;

public class VaadinGridBinder<T, E extends ObjectEditing> {

  private Grid<T> grid;
  private E editing;
  private BiFunction<E, String, T> itemGetter;

  private List<T> items;
  private Map<String, T> itemsByPath;

  public VaadinGridBinder(Grid<T> grid, E editing, String path, String collectionName,
      BiFunction<E, String, T> itemGetter) {
    this.grid = grid;
    this.editing = editing;
    this.itemGetter = itemGetter;
    this.items = new ArrayList<>();
    this.itemsByPath = new HashMap<>();
    // this.editing.publisher().onCollectionChanged(path, collectionName,
    // this::onCollectionChanged);
    this.editing.publisher().collections().subscribe().collection(path, collectionName)
        .add(this::onCollectionChanged);
    this.grid.setItems(items);
  }

  protected void onCollectionChanged(CollectionChange changes) {
    for (ObjectChange change : changes.getChanges()) {
      String itemPath = change.getPath();
      if (change.getOperation().equals(ChangeState.NEW)) {
        T item = itemGetter.apply(editing, itemPath);
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
        T item = itemGetter.apply(editing, itemPath);
        grid.getDataProvider().refreshItem(item);
      }
    }
  }

}
