package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.common.filter.FilterFieldUIState;
import org.smartbit4all.ui.common.filter.FilterLabelPosition;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterFieldUI extends FlexLayout {

  private FlexLayout header;
  // TODO remove Row
  private Div row;
  private Button btnClose;
  private FilterGroupUI group;
  private Label lblOperation;
  private Label lblFilterName;
  private FlexLayout filterLayout;
  private FilterLabelPosition position;
  private FlexLayout operationWrapper;
  private Dialog operationSelector;
  private FilterOperationUI operationUI;
  private FilterFieldUIState uiState;
  private List<Value> possibleValues;


  public <T extends Component> FilterFieldUI(FilterGroupUI group,
      FilterFieldUIState uiState, Runnable close, List<Value> possibleValues) {
    addClassName("filterfield");
    this.uiState = uiState;
    this.group = group;
    this.position = uiState.getPosition();
    this.possibleValues = possibleValues;

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

    addOperationUI(uiState.getFilter().getOperation().getFilterView());
    setPossibleOperations();

  }

  public void addOperationUI(String filterView) {

    if ("filterop.txt.eq".equals(filterView)) {
      operationUI = new FilterOperationOneFieldUI();
    } else if ("filterop.date.interval".equals(filterView)) {
      operationUI = new FilterOperationDateTimeInterval();
    } else if ("filterop.multi.eq".equals(filterView)) {
      operationUI = new FilterOperationMultiSelectUI(possibleValues);
    } else if ("filterop.combo.eq".equals(filterView)) {
      operationUI = new FilterOperationComboBoxUI(possibleValues);
    } else if ("filterop.date.interval.cb".equals(filterView)) {
      operationUI = new FilterOperationDateTimeComboBoxPicker();
    }

    row.removeAll();
    row.add(operationUI);
    // TODO selected operation doesn't change
    setOperationText(uiState.getSelectedOperation().getDisplayValue());

    if (position.equals(FilterLabelPosition.PLACEHOLDER)) {
      header.remove(lblFilterName);
      operationUI.setPlaceholder(getTranslation(uiState.getLabelCode()));
    } else if (position.equals(FilterLabelPosition.ON_LEFT)) {
      lblFilterName.setText(getTranslation(uiState.getLabelCode()));
      filterLayout.setFlexDirection(FlexDirection.ROW);
      header.remove(btnClose);
      filterLayout.add(btnClose);
    } else {
      lblFilterName.setText(getTranslation(uiState.getLabelCode()));
    }
  }

  public FilterGroupUI getGroup() {
    return group;
  }

  public void setOperationText(String label) {
    lblOperation.setText(getTranslation(label));
  }

  public void setPossibleOperations() {

    operationWrapper.addClickListener(operationClickListener());
    operationSelector = new Dialog();

    FlexLayout dialogOptionsLayout = new FlexLayout();
    dialogOptionsLayout.setFlexDirection(FlexDirection.COLUMN);
    operationSelector.add(dialogOptionsLayout);

    List<FilterOperation> possibleOperations = uiState.getOperations();
    if (possibleOperations != null) {
      for (FilterOperation operation : possibleOperations) {
        String displayValue = operation.getDisplayValue();
        Button button = new Button(displayValue);
        dialogOptionsLayout.add(button);
        button.addClickListener(e -> row.remove(operationUI));
        button.addClickListener(e -> operationSelector.close());
        button.addClickListener(e -> {
          addOperationUI(displayValue);
        });
      }
    }
  }

  private ComponentEventListener<ClickEvent<FlexLayout>> operationClickListener() {
    return e -> {
      operationSelector.open();
    };
  }

}
