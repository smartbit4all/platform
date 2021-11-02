package org.smartbit4all.ui.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;

public abstract class CollectionBinder<T> extends AbstractBinder {

  protected final ObservableObject observableObject;

  protected String[] collectionPath;
  protected final List<T> items;
  protected final Map<String, T> itemsByPath;

  protected CollectionBinder(ObservableObject observableObject, String... collectionPath) {
    this.observableObject = Objects.requireNonNull(observableObject);
    this.collectionPath = collectionPath;
    this.items = new ArrayList<>();
    this.itemsByPath = new HashMap<>();
  }

  /**
   * For any subclass to work properly, subclass constructors should call this method after all data
   * structures are initialized. This method most likely will result with a call to
   * {@link #onCollectionObjectChanged(CollectionObjectChange)}, if observable object's ref is
   * already set.
   */
  protected void registerModelObserver() {
    this.disposable = this.observableObject.onCollectionObjectChange(
        this::onCollectionObjectChanged, collectionPath);
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
    }
  }

  protected void addItem(String itemPath, T item) {
    items.add(item);
    itemsByPath.put(itemPath, item);
  }

  protected void modifyItem(String itemPath, T newValue, T oldValue) {
    if (oldValue != newValue) {
      items.replaceAll(i -> i == oldValue ? newValue : i);
      itemsByPath.put(itemPath, newValue);
    }
  }

  protected T deleteItem(String itemPath) {
    T item = itemsByPath.remove(itemPath);
    if (item != null) {
      items.remove(item);
    }
    return item;
  }

}
