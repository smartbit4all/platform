package org.smartbit4all.ui.vaadin.components.filter;

import org.smartbit4all.ui.common.filter.DynamicFilterLabelPosition;
import org.smartbit4all.ui.common.filter.FilterFieldUIState;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class DynamicFilterUI extends FlexLayout {

  private FlexLayout header;
  // TODO remove Row
  private Div row;
  private Button btnClose;
  private DynamicFilterGroupUI group;
  private Label lblOperation;
  private Label lblFilterName;
  private FlexLayout filterLayout;
  private DynamicFilterLabelPosition position;

  public DynamicFilterUI(DynamicFilterGroupUI group, FilterFieldUIState uiState) {
    addClassName("dynamic-filter");
    this.group = group;
    this.position = uiState.getPosition();

    lblFilterName = new Label();
    lblFilterName.addClassName("filter-name");
    lblOperation = new Label();
    lblOperation.addClassName("operation-name");
    row = new Div();
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

    if (uiState.isCloseable()) {
      btnClose.setText("x");
    } else {
      btnClose.setEnabled(false);
    }

  }

  public DynamicFilterUI(DynamicFilterGroupUI group, boolean isClosable,
      DynamicFilterLabelPosition position) {
    addClassName("dynamic-filter");
    this.group = group;
    this.position = position;

    lblFilterName = new Label();
    lblFilterName.addClassName("filter-name");
    lblOperation = new Label();
    lblOperation.addClassName("operation-name");
    row = new Div();
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
      header.remove(lblFilterName);
      operationUI.setPlaceholder(getTranslation(operationUI.getFilterName()));
    } else if (position.equals(DynamicFilterLabelPosition.ON_LEFT)) {
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
