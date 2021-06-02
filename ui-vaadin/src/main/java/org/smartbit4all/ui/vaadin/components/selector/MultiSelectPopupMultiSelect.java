package org.smartbit4all.ui.vaadin.components.selector;

import java.util.Set;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;

public class MultiSelectPopupMultiSelect<T> implements MultiSelect<Grid<T>, T> {

  private MultiSelectPopUp<T> popup;
  private MultiSelect<Grid<T>, T> delegate;

  public MultiSelectPopupMultiSelect(MultiSelectPopUp<T> popup, MultiSelect<Grid<T>, T> delegate) {
    this.popup = popup;
    this.delegate = delegate;
  }

  @Override
  public void setValue(Set<T> value) {
    MultiSelect.super.setValue(value);
    popup.updateSelection();
  }

  @Override
  public Registration addValueChangeListener(
      ValueChangeListener<? super ComponentValueChangeEvent<Grid<T>, Set<T>>> listener) {
    return delegate.addValueChangeListener(listener);
  }

  @Override
  public Element getElement() {
    return delegate.getElement();
  }

  @Override
  public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
    delegate.updateSelection(addedItems, removedItems);
  }

  @Override
  public Set<T> getSelectedItems() {
    return delegate.getSelectedItems();
  }

  @Override
  public Registration addSelectionListener(MultiSelectionListener<Grid<T>, T> listener) {
    return delegate.addSelectionListener(listener);
  }

}
