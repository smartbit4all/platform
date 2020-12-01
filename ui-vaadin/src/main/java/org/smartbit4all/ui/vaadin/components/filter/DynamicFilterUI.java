package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.ui.common.filter.DynamicFilterDateOperation;
import org.smartbit4all.ui.common.filter.DynamicFilterLabelPosition;
import org.smartbit4all.ui.common.filter.FilterFieldUIState;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
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
  private FlexLayout operationWrapper;
  private Dialog operationSelector;
  private DynamicFilterDateOperation operation;
  private List<String> possibleOperations;
  private DynamicFilterOperationUI operationUI;

  public <T extends Component> DynamicFilterUI(DynamicFilterGroupUI group,
      FilterFieldUIState uiState, Runnable close) {
    addClassName("dynamic-filter");
    this.group = group;
    this.position = uiState.getPosition();

    lblFilterName = new Label();
    lblFilterName.addClassName("filter-name");

    operationWrapper = new FlexLayout();
    lblOperation = new Label();
    lblOperation.addClassName("operation-name");
    operationWrapper.add(lblOperation);
    row = new Div();
    row.addClassName("filter-row");

    btnClose = new Button(" ");
    btnClose.addClassName("close-button");


    header = new FlexLayout();
    header.addClassName("filter-header");
    header.add(lblFilterName, operationWrapper, btnClose);

    filterLayout = new FlexLayout();
    filterLayout.addClassName("filter-layout");
    filterLayout.add(header, row);
    add(filterLayout);

    if (uiState.isCloseable()) {
      btnClose.setText("x");
      btnClose.addClickListener(e -> close.run());
    } else {
      btnClose.setEnabled(false);
    }

  }

  public DynamicFilterUI(DynamicFilterGroupUI groupUI, FilterFieldUIState filterUIState,
      Object close) {
    // TODO Auto-generated constructor stub
  }

  public void addOperationUI(DynamicFilterOperationUI operationUI) {
    row.add(operationUI);
    this.operationUI = operationUI;

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

  public void setOperation(String label) {
    lblOperation.setText(getTranslation(label));

  }

  public void setPossibleOperations(List<String> possibleOperations) {
    this.possibleOperations = possibleOperations;

    operationWrapper.addClickListener(operationClickListener());
    operationSelector = new Dialog();

    FlexLayout dialogOptionsLayout = new FlexLayout();
    dialogOptionsLayout.setFlexDirection(FlexDirection.COLUMN);
    operationSelector.add(dialogOptionsLayout);

    if (possibleOperations != null) {
      for (String operation : possibleOperations) {
        Button button = new Button(operation);
        dialogOptionsLayout.add(button);
        button.addClickListener(e -> row.remove(operationUI));
        button.addClickListener(e -> operationSelector.close());
        if (operation.contentEquals("Intervallum")) {
          button.addClickListener(
              e -> {
                DynamicFilterOperationDateTimeInterval dynamicFilterOperationDateTimeInterval =
                    new DynamicFilterOperationDateTimeInterval("Intervallum");
                operationUI = dynamicFilterOperationDateTimeInterval;
                row.add(dynamicFilterOperationDateTimeInterval);
                lblOperation.setText("Intervallum");
              });
        } else {
          button.addClickListener(
              e -> {
                DynamicFilterOperationDateIntervalPicker dynamicFilterOperationDateIntervalPicker =
                    new DynamicFilterOperationDateIntervalPicker("Időszakok");
                operationUI = dynamicFilterOperationDateIntervalPicker;
                row.add(dynamicFilterOperationDateIntervalPicker);
                lblOperation.setText("Időszakok");
              });
        }
      }
    }
  }

  private ComponentEventListener<ClickEvent<FlexLayout>> operationClickListener() {
    return e -> {
      operationSelector.open();
    };
  }

}
