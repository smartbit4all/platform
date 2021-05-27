package org.smartbit4all.ui.vaadin.components.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;

public abstract class VaadinCollectionBinder<T> extends VaadinAbstractBinder {

  protected final ObservableObject observableObject;

  protected final List<T> items;
  protected final Map<String, T> itemsByPath;

  protected VaadinCollectionBinder(ObservableObject observableObject, String path,
      String collectionName) {
    this.observableObject = Objects.requireNonNull(observableObject);
    this.items = new ArrayList<>();
    this.itemsByPath = new HashMap<>();
    this.disposable = this.observableObject.onCollectionObjectChange(path, collectionName,
        this::onCollectionObjectChanged);
  }

  @SuppressWarnings("unchecked")
  protected void onCollectionObjectChanged(CollectionObjectChange changes) {
    for (ObjectChangeSimple change : changes.getChanges()) {
      String itemPath = change.getPath();
      T item = null;
      if (change.getOperation().equals(ChangeState.NEW) ||
          change.getOperation().equals(ChangeState.MODIFIED)) {
        item = (T) change.getObject();
        T oldValue = itemsByPath.get(itemPath);
        if (oldValue == null) {
          // NEW
          addItem(itemPath, item);
        } else {
          // MODIFY
          modifyItem(itemPath, item, oldValue);
        }
      } else if (change.getOperation().equals(ChangeState.DELETED)) {
        item = deleteItem(itemPath);
      }
      handleItemRefreshed(item);
    }
    // TODO make item refreshes in one call, if possible.
  }

  private void addItem(String itemPath, T item) {
    items.add(item);
    itemsByPath.put(itemPath, item);
  }

  private void modifyItem(String itemPath, T newValue, T oldValue) {
    if (oldValue != newValue) {
      items.replaceAll(i -> i == oldValue ? newValue : i);
      itemsByPath.put(itemPath, newValue);
    }
  }

  private T deleteItem(String itemPath) {
    T item = itemsByPath.remove(itemPath);
    if (item != null) {
      items.remove(item);
    }
    return item;
  }

  protected void handleItemRefreshed(T item) {
    // intentionally left blank
  }

}
