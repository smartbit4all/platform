package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.common.filter.DynamicFilterLabelPosition;
import org.smartbit4all.ui.vaadin.util.IconSize;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DynamicFilterUI extends FlexLayout {

  private FlexLayout header;
  private Row row;
  private Button btnClose;
  private DynamicFilterGroupUI group;
  private Label lblOperation;
  private Label lblFilterName;
  private FlexLayout filterLayout;
  private DynamicFilterLabelPosition position;


  public DynamicFilterUI(DynamicFilterGroupUI group, boolean isClosable, DynamicFilterLabelPosition position) {
    addClassName("dynamic-filter");
    this.group = group;
    this.position = position;
    
    lblFilterName = new Label();
    lblFilterName.addClassName("filter-name");
    lblOperation = new Label();    
    lblOperation.addClassName("operation-name");
    row = new Row();
    row.addClassName("filter-row");
    
    btnClose = new Button(" ");
    btnClose.addClassName("close-button");
    
    
    header = new FlexLayout();
    header.addClassName("filter-header");
    header.add(lblFilterName, lblOperation, btnClose);

    filterLayout = new FlexLayout();
    filterLayout.addClassName("filter-layout");
    filterLayout.add(header, row);
    add(filterLayout);
    
    if (isClosable) {
      btnClose.setText("x");
    } else {
      btnClose.setEnabled(false);
    }
    
  }

  public void addOperationUI(DynamicFilterOperationUI operationUI) {
    row.add(operationUI);
    
    if (position.equals(DynamicFilterLabelPosition.PLACEHOLDER)) {
      lblFilterName.setText("");
      operationUI.setPlaceholder(getTranslation(operationUI.getFilterName()));
    } else if (position.equals(DynamicFilterLabelPosition.ON_LEFT)){
      lblFilterName.setText(getTranslation(operationUI.getFilterName()));
      filterLayout.setFlexDirection(FlexDirection.ROW);
      header.remove(btnClose);
      filterLayout.add(btnClose);
    } else {
      lblFilterName.setText(getTranslation(operationUI.getFilterName()));
    }
  }


  public DynamicFilterGroupUI getGroup() {
    return group;
  }

  public Button getCloseButton() {
    return btnClose;
  }

  public void setOperationText(String label) {
    lblOperation.setText(getTranslation(label));
  }
  


}
