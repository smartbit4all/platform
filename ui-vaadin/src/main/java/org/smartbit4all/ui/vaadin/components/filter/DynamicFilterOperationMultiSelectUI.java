package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.listbox.MultiSelectListBox;

public class DynamicFilterOperationMultiSelectUI extends Div{
  List<String> items;
  MultiSelectListBox<String> listBox;
  
  public DynamicFilterOperationMultiSelectUI(List<String> items) {
    this.items = items;
    listBox = new MultiSelectListBox<String>();
    listBox.setItems(items);
    add(listBox);
  }
  
  
}
