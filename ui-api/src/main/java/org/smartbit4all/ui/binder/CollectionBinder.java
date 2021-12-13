package org.smartbit4all.ui.binder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;

public abstract class CollectionBinder<T> extends AbstractBinder {

  protected final ObservableObject observableObject;

  protected String[] collectionPath;
  protected final List<T> items;
  protected final List<String> itemPaths;

  protected CollectionBinder(ObservableObject observableObject, String... collectionPath) {
    this.observableObject = Objects.requireNonNull(observableObject);
    this.collectionPath = collectionPath;
    this.items = new ArrayList<>();
    this.itemPaths = new ArrayList<>();
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
        int idx = itemPaths.indexOf(itemPath);
        if (idx < 0) {
          // NEW
          addItem(itemPath, item);
        } else {
          // MODIFY
          modifyItem(idx, item);
        }
      } else if (change.getOperation().equals(ChangeState.DELETED)) {
        item = deleteItem(itemPath);
      }
    }
  }

  protected void addItem(String itemPath, T item) {
    if (itemPaths.contains(itemPath)) {
      throw new IllegalArgumentException("ItemPath added more than once: " + itemPath);
    }
    itemPaths.add(itemPath);
    items.add(item);

  }

  protected T deleteItem(String itemPath) {
    int idx = itemPaths.indexOf(itemPath);
    return deleteItemByIndex(idx);
  }

  protected T deleteItemByIndex(int idx) {
    itemPaths.remove(idx);
    return items.remove(idx);
  }

  protected void modifyItem(int idx, T newItem) {
    items.set(idx, newItem);
  }

}
