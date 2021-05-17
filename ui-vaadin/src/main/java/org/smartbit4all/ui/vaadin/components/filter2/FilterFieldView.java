package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.ui.common.filter.FilterLabelPosition;
import org.smartbit4all.ui.common.filter2.model.FilterLabel;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterFieldView extends FlexLayout implements DragSource<FilterFieldView> {

  private ObservableObject filterField;

  private FlexLayout header;
  private Div row;
  private Button btnClose;
  private Label lblOperation;
  private Label lblFilterName;
  private FlexLayout filterLayout;
  private FlexLayout operationWrapper;
  private Dialog operationSelector;
  private FilterOperationView operationView;
  private String currentFilterView;

  private FlexLayout optionsLayout;

  public FilterFieldView(ObservableObject filterField) {
    this.filterField = filterField;
    createUI();
  }

  private void createUI() {
    addClassName("filterfield");
    lblFilterName = new Label();
    lblFilterName.addClassName("filter-name");

    operationWrapper = new FlexLayout();
    lblOperation = new Label();
    lblOperation.addClassName("operation-name");
    operationWrapper.add(lblOperation);
    operationWrapper.addClickListener(e -> operationSelector.open());
    operationSelector = new Dialog();

    optionsLayout = new FlexLayout();
    optionsLayout.setFlexDirection(FlexDirection.COLUMN);
    operationSelector.add(optionsLayout);


    row = new Div();
    row.addClassName("filter-row");

    btnClose = new Button(" ");
    btnClose.addClassName("close-button");
    btnClose.setEnabled(false);
    // TODO close button command
    // btnClose.addClickListener(e -> controller.removeFilterField(group.getGroupId(), filterId));

    header = new FlexLayout();
    header.addClassName("filter-header");
    header.add(lblFilterName, operationWrapper, btnClose);

    filterLayout = new FlexLayout();
    filterLayout.addClassName("filter-layout");
    filterLayout.add(header, row);
    add(filterLayout);


    filterField.onPropertyChange(null, "draggable", this::draggableChange);
    filterField.onPropertyChange(null, "closeable", this::closeableChange);
    filterField.onReferencedObjectChange(null, "label", this::labelChange);
    filterField.onCollectionObjectChange(null, "operations", this::onOperationsChange);
    filterField.onPropertyChange("selectedOperation", "filterView", this::operationViewChange);
    VaadinBinders.bind(lblOperation, filterField, "selectedOperation/labelCode")
        .setConverterFunction(s -> getTranslation((String) s));
  }

  private void onOperationsChange(CollectionObjectChange changes) {
    if (operationSelector != null && operationSelector.isOpened()) {
      operationSelector.close();
    }
    for (ObjectChangeSimple change : changes.getChanges()) {
      if (change.getOperation() == ChangeState.NEW) {
        FilterOperation operation = (FilterOperation) change.getObject();
        String labelCode = operation.getLabelCode();
        Button button = new Button(getTranslation(labelCode));
        optionsLayout.add(button);
        button.addClickListener(
            e -> filterField.setValue("selectedOperation", operation));
      }
      // TODO handle modify / delete
    }
  }

  private void draggableChange(PropertyChange change) {
    if (Boolean.TRUE.equals(change.getNewValue())) {
      setDraggable(true);
      setDragData(this);
    } else {
      setDraggable(false);
    }
  }

  private void closeableChange(PropertyChange change) {
    boolean isCloseable = Boolean.TRUE.equals(change.getNewValue());
    if (isCloseable && !btnClose.isEnabled()) {
      btnClose.setText("x");
      btnClose.setEnabled(true);
    }
    if (!isCloseable && btnClose.isEnabled()) {
      btnClose.setText("");
      btnClose.setEnabled(false);
    }
  }

  private void labelChange(ReferencedObjectChange change) {
    FilterLabel label = (FilterLabel) change.getChange().getObject();
    FilterLabelPosition position = label.getPosition();
    String text = getLabelOfFilter(label.getCode(), label.getDuplicateNum());
    if (position.equals(FilterLabelPosition.PLACEHOLDER)) {
      header.remove(lblFilterName);
      operationView.setPlaceholder(text);
    } else if (position.equals(FilterLabelPosition.ON_LEFT)) {
      lblFilterName.setText(text);
      filterLayout.setFlexDirection(FlexDirection.ROW);
      header.remove(btnClose);
      filterLayout.add(btnClose);
    } else {
      lblFilterName.setText(text);
    }
  }

  private void operationViewChange(PropertyChange change) {
    String filterView = (String) change.getNewValue();
    if (!filterView.equals(currentFilterView)) {
      currentFilterView = filterView;
      if ("filterop.txt.eq".equals(filterView)) {
        operationView = new FilterOperationTxtEqualsView(filterField);
        // } else if ("filterop.txt.like".equals(filterView)) {
        // operationView = new FilterOperationTxtLikeView(viewModel);
        // } else if ("filterop.txt.like.min".equals(filterView)) {
        // operationView = new FilterOperationTxtLikeMinView(viewModel);
        // } else if ("filterop.multi.eq".equals(filterView)) {
        // operationView = new FilterOperationMultiSelectView(viewModel, possibleValues);
        // } else if ("filterop.combo.eq".equals(filterView)) {
        // operationView = new FilterOperationComboBoxView(viewModel, possibleValues);
        // } else if ("filterop.date.time.interval".equals(filterView)) {
        // operationView = new FilterOperationDateTimeIntervalView(viewModel);
        // } else if ("filterop.date.time.interval.cb".equals(filterView)) {
        // operationView = new FilterOperationDateTimeComboBoxPickerView(viewModel);
        // } else if ("filterop.date.time.eq".equals(filterView)) {
        // operationView = new FilterOperationDateTimeEqualsView(viewModel);
        // } else if ("filterop.date.interval".equals(filterView)) {
        // operationView = new FilterOperationDateIntervalView(viewModel);
        // } else if ("filterop.date.interval.cb".equals(filterView)) {
        // operationView = new FilterOperationDateComboBoxPickerView(viewModel);
        // } else if ("filterop.date.eq".equals(filterView)) {
        // operationView = new FilterOperationDateEqualsView(viewModel);
      } else {
        throw new IllegalArgumentException("Invalid filterView parameter (" + filterView
            + ") in filter!");
      }

      row.removeAll();
      row.add(operationView);
    }
  }

  private String getLabelOfFilter(String labelCode, int duplicateNum) {
    String label = getTranslation(labelCode);
    if (duplicateNum > 0) {
      label = createLabelWithDuplicateIndicator(label, duplicateNum);
    }
    return label;
  }

  private String createLabelWithDuplicateIndicator(String label, int duplicateNum) {
    label = label + " (" + ++duplicateNum + ")";
    return label;
  }

}
