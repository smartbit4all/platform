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
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.ironlist.IronList;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.HasItems;

public class VaadinHasItemsBinder<T> {

  private HasItems<T> list;
  private ObservableObject observableObject;

  private List<T> items;
  private Map<String, T> itemsByPath;

  public VaadinHasItemsBinder(HasItems<T> list, ObservableObject observableObject, String path,
      String collectionName) {
    Objects.requireNonNull(list);
    this.list = list;
    this.observableObject = observableObject;
    this.items = new ArrayList<>();
    this.itemsByPath = new HashMap<>();
    this.observableObject.onCollectionObjectChange(path, collectionName,
        this::onCollectionObjectChanged);
    this.list.setItems(items);
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
      refreshItem(item);
    }
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

  private void refreshItem(T item) {
    if (item == null) {
      return;
    }
    if (list instanceof Grid) {
      ((Grid<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof ComboBox) {
      ((ComboBox<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof CheckboxGroup) {
      ((CheckboxGroup<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof IronList) {
      ((IronList<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof ListBox) {
      ((ListBox<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof MultiSelectListBox) {
      ((MultiSelectListBox<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof MultiSelectPopUp) {
      ((MultiSelectPopUp<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof RadioButtonGroup) {
      ((RadioButtonGroup<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof Select) {
      ((Select<T>) list).getDataProvider().refreshItem(item);
    } else if (list instanceof TreeGrid) {
      ((TreeGrid<T>) list).getDataProvider().refreshItem(item);
    } else {
      throw new IllegalArgumentException(
          "Unhandled HasItems list class: " + list.getClass().getName());
    }
  }
}
