/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.common.filter.DynamicFilterController;
import org.smartbit4all.ui.common.filter.FilterFieldUIState;
import org.smartbit4all.ui.common.filter.FilterLabelPosition;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterFieldUI extends FlexLayout implements DragSource<FilterFieldUI> {

  private FlexLayout header;
  // TODO remove Row
  private Div row;
  private Button btnClose;
  private FilterGroupUI group;
  private Label lblOperation;
  private Label lblFilterName;
  private FlexLayout filterLayout;
  private FlexLayout operationWrapper;
  private Dialog operationSelector;
  private FilterOperationUI operationUI;
  private String filterId;
  private DynamicFilterController controller;


  public <T extends Component> FilterFieldUI(FilterGroupUI group,
      FilterFieldUIState uiState, DynamicFilterController controller) {
    this.filterId = uiState.getId();
    this.controller = controller;
    setDraggable(true);
    setDragData(this);
    addClassName("filterfield");
    this.group = group;

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


    // updateOperationUI(uiState.getFilter().getOperation().getFilterView());
    updateState(uiState);
  }

  public String getFilterId() {
    return filterId;
  }

  public void updateState(FilterFieldUIState uiState) {

    String filterView = uiState.getSelectedOperation().getFilterView();
    FilterLabelPosition position = uiState.getPosition();
    List<Value> possibleValues = uiState.getPossibleValues();

    if ("filterop.txt.eq".equals(filterView)) {
      operationUI = new FilterOperationOneFieldUI();
    } else if ("filterop.multi.eq".equals(filterView)) {
      operationUI = new FilterOperationMultiSelectUI(possibleValues);
    } else if ("filterop.combo.eq".equals(filterView)) {
      operationUI = new FilterOperationComboBoxUI(possibleValues);
    } else if ("filterop.date.time.interval".equals(filterView)) {
      operationUI = new FilterOperationDateTimeInterval();
    } else if ("filterop.date.time.interval.cb".equals(filterView)) {
      operationUI = new FilterOperationDateTimeComboBoxPicker();
    } else if ("filterop.date.time.eq".equals(filterView)) {
      operationUI = new FilterOperationDateTimeEquals();
    } else if ("filterop.date.interval".equals(filterView)) {
      operationUI = new FilterOperationDateInterval();
    } else if ("filterop.date.interval.cb".equals(filterView)) {
      operationUI = new FilterOperationDateComboBoxPicker();
    } else if ("filterop.date.eq".equals(filterView)) {
      operationUI = new FilterOperationDateEquals();
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

    setPossibleOperations(uiState.getOperations());

    if (uiState.isCloseable()) {
      btnClose.setText("x");
      btnClose.addClickListener(e -> controller.removeFilterField(group.getGroupId(), filterId));
    } else {
      btnClose.setText("");
      btnClose.setEnabled(false);
    }

  }

  public FilterGroupUI getGroup() {
    return group;
  }

  public void setGroup(FilterGroupUI group) {
    this.group = group;
  }

  public void setOperationText(String label) {
    lblOperation.setText(getTranslation(label));
  }

  private void setPossibleOperations(List<FilterOperation> possibleOperations) {

    if (operationSelector != null && operationSelector.isOpened()) {
      operationSelector.close();
    }
    operationWrapper.addClickListener(operationClickListener());
    operationSelector = new Dialog();

    FlexLayout dialogOptionsLayout = new FlexLayout();
    dialogOptionsLayout.setFlexDirection(FlexDirection.COLUMN);
    operationSelector.add(dialogOptionsLayout);

    if (possibleOperations != null) {
      for (FilterOperation operation : possibleOperations) {
        String displayValue = operation.getDisplayValue();
        Button button = new Button(displayValue);
        dialogOptionsLayout.add(button);
        button.addClickListener(e -> {
          controller.filterOperationChanged(filterId, operation.getCode());
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
