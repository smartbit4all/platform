package org.smartbit4all.ui.vaadin.components.filter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DynamicFilterUI extends FlexLayout {

  private FlexLayout header;
  private Row row;
  private Button btnClose;
  private DynamicFilterGroupUI group;
  private Label lblOperation;
  private Label lblFilterName;
  

  public DynamicFilterUI(DynamicFilterGroupUI group) {
    setFlexDirection(FlexDirection.COLUMN);
    addClassName("dynamic-filter");
    this.group = group;
    row = new Row();
    row.addClassName("filter-row");
    header = new FlexLayout();
    header.addClassName("filter-header");
    lblFilterName = new Label();
    lblOperation = new Label();
    lblOperation.addClassName("operation-name");
    btnClose = new Button("x");
    btnClose.addClassName("close-button");
    
    header.add(lblFilterName, lblOperation, btnClose);
    add(header);
    add(row);
  }
  
  public void addOperation(DynamicFilterOperationOneFieldUI component) {
    row.add(component);
    
    lblFilterName.setText(getTranslation(component.getLabel()));
    lblFilterName.addClassName("filter-name");
    component.setLabel("");
  }
  
  
  public DynamicFilterGroupUI getGroup() {
    return group;
  }
  
  public Button getButton() {
    return btnClose;
  }
  
  public void setLabel(String label) {
    lblOperation.setText(getTranslation(label));
  }

  
}
