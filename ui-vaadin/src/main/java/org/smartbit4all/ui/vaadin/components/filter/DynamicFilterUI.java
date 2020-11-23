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

  public void addOperation(DynamicFilterOperationOneFieldUI component) {
    row.add(component);

    lblFilterName.setText(getTranslation(component.getLabel()));
    lblFilterName.addClassName("filter-name");
    
    if (position.equals(DynamicFilterLabelPosition.PLACEHOLDER)) {
      lblFilterName.setText("");
      component.getTextField().setPlaceholder(getTranslation(component.getLabel()));
    } else if (position.equals(DynamicFilterLabelPosition.ON_LEFT)){
      addClassName("filter-side-label");
      filterLayout.setFlexDirection(FlexDirection.ROW);
      header.remove(btnClose);
      filterLayout.add(btnClose);
    }
    component.setLabel("");
  }


  public DynamicFilterGroupUI getGroup() {
    return group;
  }

  public Button getCloseButton() {
    return btnClose;
  }

  public void setLabel(String label) {
    lblOperation.setText(getTranslation(label));
  }
  


}
