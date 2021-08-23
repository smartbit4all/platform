package org.smartbit4all.ui.vaadin.components.filter2;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.api.filter.model.FilterFieldLabel;
import org.smartbit4all.ui.api.filter.model.FilterLabelPosition;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.util.Css;
import org.smartbit4all.ui.vaadin.util.Layouts;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterFieldView extends FlexLayout implements DragSource<FilterFieldView> {

  private ObjectEditing viewModel;
  private ObservableObject filterField;
  private String path;

  private FlexLayout header;
  private Div row;
  private Button btnClose;
  private Label lblFilterName;
  private FlexLayout filterLayout;
  private Dialog operationSelector;
  private FilterOperationView operationView;
  private String currentFilterView;

  private FlexLayout optionsLayout;

  private Map<String, Button> operations = new HashMap<>();

  private boolean filterEnabled;
  private boolean closeable;

  public FilterFieldView(ObjectEditing viewModel, ObservableObject filterField, String path) {
    this.filterField = filterField;
    this.viewModel = viewModel;
    this.path = path;
    this.filterEnabled = true;
    createUI();
  }

  private void createUI() {
    addClassName("filterfield");
    lblFilterName = new Label();
    lblFilterName.addClassName("filter-name");

    FlexLayout operationWrapper = new FlexLayout();
    Label lblOperation = new Label();
    lblOperation.addClassName("operation-name");
    operationWrapper.add(lblOperation);
    operationWrapper.addClickListener(this::openOperationSelector);
    Css.stopClickEventPropagation(operationWrapper);
    operationSelector = new Dialog();

    optionsLayout = new FlexLayout();
    optionsLayout.setFlexDirection(FlexDirection.COLUMN);
    operationSelector.add(optionsLayout);

    row = new Div();
    row.addClassName("filter-row");

    btnClose = new Button(" ");
    btnClose.addClassName("close-button");
    btnClose.addClickListener(e -> viewModel.executeCommand(path, "CLOSE_FILTERFIELD"));
    btnClose.setEnabled(false);

    header = new FlexLayout();
    header.addClassName("filter-header");
    header.add(lblFilterName, operationWrapper, btnClose);

    filterLayout = new FlexLayout();
    filterLayout.addClassName("filter-layout");
    filterLayout.add(header, row);
    add(filterLayout);


    filterField.onPropertyChange(path, "draggable", this::draggableChange);
    filterField.onPropertyChange(path, "closeable", this::closeableChange);
    filterField.onPropertyChange(path, "enabled", this::enabledChange);
    filterField.onCollectionObjectChange(path, "operations", this::onOperationsChange);
    filterField.onPropertyChange(PathUtility.concatPath(path, "selectedOperation"), "filterView",
        this::operationViewChange);
    filterField.onReferencedObjectChange(path, "label", this::labelChange);
    VaadinBinders.bind(lblOperation, filterField,
        PathUtility.concatPath(path, "selectedOperation/labelCode"),
        s -> getTranslation((String) s));
  }

  private void openOperationSelector(ClickEvent<FlexLayout> e) {
    if (optionsLayout.getComponentCount() > 1) {
      // if 0 or 1 available operation exists, selection is not enabled
      operationSelector.open();
    }
  }

  private void onOperationsChange(CollectionObjectChange changes) {
    if (operationSelector != null && operationSelector.isOpened()) {
      operationSelector.close();
    }
    for (ObjectChangeSimple change : changes.getChanges()) {
      String operationPath = change.getPath();
      if (change.getOperation() == ChangeState.NEW) {
        if (operations.get(operationPath) == null) {
          FilterOperation operation = (FilterOperation) change.getObject();
          String labelCode = operation.getLabelCode();
          Button button = new Button(getTranslation(labelCode));
          optionsLayout.add(button);
          button.addClickListener(
              e -> {
                viewModel.executeCommand(path, "CHANGE_OPERATION", operationPath);
                operationSelector.close();
              });
          operations.put(operationPath, button);
        }
      } else if (change.getOperation() == ChangeState.MODIFIED) {
        // NOP
      } else if (change.getOperation() == ChangeState.DELETED) {
        Button button = operations.get(operationPath);
        Layouts.removeFromLayout(optionsLayout, button);
        operations.remove(operationPath);
      }
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
    closeable = Boolean.TRUE.equals(change.getNewValue());
    refreshCloseButton();
  }

  private void refreshCloseButton() {
    boolean effectiveEnabled = closeable && filterEnabled;
    if (effectiveEnabled && !btnClose.isEnabled()) {
      btnClose.setText("x");
      btnClose.setEnabled(true);
    }
    if (!effectiveEnabled && btnClose.isEnabled()) {
      btnClose.setText("");
      btnClose.setEnabled(false);
    }
  }

  private void enabledChange(PropertyChange change) {
    filterEnabled = Boolean.TRUE.equals(change.getNewValue());
    if (operationView != null) {
      operationView.setEnabled(filterEnabled);
    }
    refreshCloseButton();
  }

  private void labelChange(ReferencedObjectChange change) {
    FilterFieldLabel label = (FilterFieldLabel) change.getChange().getObject();
    FilterLabelPosition position = label.getPosition();
    String text = getLabelOfFilter(label.getCode(), label.getDuplicateNum());
    if (position.equals(FilterLabelPosition.PLACEHOLDER)) {
      Layouts.removeFromLayout(header, lblFilterName);
      operationView.setPlaceholder(text);
    } else if (position.equals(FilterLabelPosition.ON_LEFT)) {
      lblFilterName.setText(text);
      filterLayout.setFlexDirection(FlexDirection.ROW);
      Layouts.removeFromLayout(header, btnClose);
      Layouts.addToLayout(filterLayout, btnClose);
    } else {
      lblFilterName.setText(text);
    }
  }

  private void operationViewChange(PropertyChange change) {
    String filterView = (String) change.getNewValue();
    if (!filterView.equals(currentFilterView)) {
      FilterOperationView prevOperationView = operationView;
      if ("filterop.txt.eq".equals(filterView)) {
        operationView = new FilterOperationTxtEqualsView(filterField, path);
      } else if ("filterop.txt.like".equals(filterView)) {
        operationView = new FilterOperationTxtLikeView(filterField, path);
      } else if ("filterop.txt.like.min".equals(filterView)) {
        operationView = new FilterOperationTxtLikeMinView(filterField, path);
      } else if ("filterop.number.eq".equals(filterView)) {
        operationView = new FilterOperationNumberEqualsView(filterField, path);
      } else if ("filterop.multi.eq".equals(filterView)) {
        operationView = new FilterOperationMultiSelectView(filterField, path);
      } else if ("filterop.combo.eq".equals(filterView)) {
        operationView = new FilterOperationComboBoxView(filterField, path);
      } else if ("filterop.date.time.interval".equals(filterView)) {
        operationView = new FilterOperationDateTimeIntervalView(filterField, path);
      } else if ("filterop.date.time.interval.cb".equals(filterView)) {
        operationView = new FilterOperationDateTimeComboBoxPickerView(filterField, path);
      } else if ("filterop.date.time.eq".equals(filterView)) {
        operationView = new FilterOperationDateTimeEqualsView(filterField, path);
      } else if ("filterop.date.interval".equals(filterView)) {
        operationView = new FilterOperationDateIntervalView(filterField, path);
      } else if ("filterop.date.interval.cb".equals(filterView)) {
        operationView = new FilterOperationDateComboBoxPickerView(filterField, path);
      } else if ("filterop.date.eq".equals(filterView)) {
        operationView = new FilterOperationDateEqualsView(filterField, path);
      } else {
        throw new IllegalArgumentException("Invalid filterView parameter (" + filterView
            + ") in filter!");
      }

      if (prevOperationView != null) {
        prevOperationView.unbind();
      }
      currentFilterView = filterView;
      row.removeAll();
      operationView.setEnabled(filterEnabled);
      Css.stopClickEventPropagation(operationView);
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

  public String getPath() {
    return path;
  }

}
