package org.smartbit4all.ui.vaadin.components.binder;

import java.util.Objects;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridSingleSelectionModel;
import com.vaadin.flow.component.ironlist.IronList;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.HasItems;

public class VaadinHasItemsBinder<T> extends VaadinCollectionBinder<T> {

  private HasItems<T> list;

  public VaadinHasItemsBinder(HasItems<T> list, ObservableObject observableObject, String path,
      String collectionName) {
    super(observableObject, path, collectionName);
    this.list = Objects.requireNonNull(list);
    this.list.setItems(items);
  }

  @Override
  protected void handleItemRefreshed(T item) {
    if (list == null) {
      // called from super constructor
      return;
    }
    if (item == null) {
      return;
    }
    if (list instanceof Grid) {
      GridSelectionModel<T> selectionModel = ((Grid<T>) list).getSelectionModel();
      ((Grid<T>) list).getDataProvider().refreshAll();
      if (selectionModel != null) {
        if (selectionModel instanceof GridSingleSelectionModel) {
          ((Grid<T>) list).setSelectionMode(SelectionMode.SINGLE);
        } else {
          ((Grid<T>) list).setSelectionMode(SelectionMode.MULTI);
        }
      }
    } else if (list instanceof ComboBox) {
      ((ComboBox<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof CheckboxGroup) {
      ((CheckboxGroup<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof IronList) {
      ((IronList<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof ListBox) {
      ((ListBox<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof MultiSelectListBox) {
      ((MultiSelectListBox<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof MultiSelectPopUp) {
      ((MultiSelectPopUp<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof RadioButtonGroup) {
      ((RadioButtonGroup<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof Select) {
      ((Select<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof TreeGrid) {
      ((TreeGrid<T>) list).getDataProvider().refreshAll();
    } else {
      throw new IllegalArgumentException(
          "Unhandled HasItems list class: " + list.getClass().getName());
    }
  }
}
