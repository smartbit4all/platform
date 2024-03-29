package org.smartbit4all.ui.vaadin.components.binder;

import java.util.Objects;
import java.util.Set;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.binder.CollectionBinder;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUpList;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.GridSingleSelectionModel;
import com.vaadin.flow.component.ironlist.IronList;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.binder.HasItems;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.function.ValueProvider;

public class VaadinHasItemsBinder<T> extends CollectionBinder<T> {

  private HasItems<T> list;

  public VaadinHasItemsBinder(HasItems<T> list, ObservableObject observableObject,
      ValueProvider<T, Object> idGetter, String... collectionPath) {
    super(observableObject, collectionPath);
    registerModelObserver();
    this.list = Objects.requireNonNull(list);
    if (list instanceof HasDataProvider) {
      HasDataProvider<T> hasDataProvider = (HasDataProvider<T>) list;
      hasDataProvider.setDataProvider(new ListDataProviderWithId<>(items, idGetter));
    } else {
      this.list.setItems(items);
    }
  }

  @Override
  protected void onCollectionObjectChanged(CollectionObjectChange changes) {
    super.onCollectionObjectChanged(changes);
    handleItemRefreshed();
  }

  protected void handleItemRefreshed() {
    if (list == null) {
      // called from super constructor
      return;
    }
    if (list instanceof Grid) {
      Grid<T> grid = (Grid<T>) list;

      // FIXME
      // Getting error with "((Grid<T>) list).getDataProvider().refreshAll();" when using GridPro
      // with editable field that refreshes other Grid fields
      if ("com.vaadin.flow.component.gridpro.GridPro".equals(list.getClass().getName())) {
        GridSelectionModel<T> selectionModel = ((Grid<T>) list).getSelectionModel();

        // Save selection before setting items
        if (selectionModel != null && selectionModel.getSelectedItems() != null
            && !selectionModel.getSelectedItems().isEmpty()) {

          if (selectionModel instanceof GridSingleSelectionModel) {
            SingleSelect<Grid<T>, T> asSingleSelect = grid.asSingleSelect();
            T value = asSingleSelect.getValue();
            grid.setItems(items);
            asSingleSelect.setValue(value);
          } else {
            MultiSelect<Grid<T>, T> asMultiSelect = grid.asMultiSelect();
            Set<T> value = asMultiSelect.getValue();
            grid.setItems(items);
            grid.asMultiSelect().setValue(value);
          }
        } else {
          grid.setItems(items);
        }
      } else {
        ((Grid<T>) list).getDataProvider().refreshAll();
      }
    } else if (list instanceof ComboBox) {
      ((ComboBox<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof CheckboxGroup) {
      CheckboxGroup<T> checkboxGroup = (CheckboxGroup<T>) list;
      Set<T> value = checkboxGroup.getValue();
      checkboxGroup.getDataProvider().refreshAll();
      checkboxGroup.setValue(value);
    } else if (list instanceof IronList) {
      ((IronList<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof ListBox) {
      ((ListBox<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof MultiSelectListBox) {
      ((MultiSelectListBox<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof MultiSelectPopUp) {
      ((MultiSelectPopUp<T>) list).getDataProvider().refreshAll();
    } else if (list instanceof MultiSelectPopUpList) {
      ((MultiSelectPopUpList<T>) list).getDataProvider().refreshAll();
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
