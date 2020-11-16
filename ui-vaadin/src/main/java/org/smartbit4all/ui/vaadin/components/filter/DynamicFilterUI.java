package org.smartbit4all.ui.vaadin.components.filter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DynamicFilterUI extends FlexLayout {

  private Row row;
  private Button btnClose;
  
  public DynamicFilterUI() {
    addClassName("dynamic-filter");
    row = new Row();
    row.addClassName("filter-row");
    add(row);
    btnClose = new Button("x");
    btnClose.addClassName("close-button");
    add(btnClose);
  }
  
  public void addOperation(Component component) {
    row.add(component);
  }
  
}
