package org.smartbit4all.ui.vaadin.components.form;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FormDataContentView extends FlexLayout {
  
  private ObservableObject formDataContent;
  private ObjectEditing viewModel;
  private String path;
  
  private Map<String, FormInputView> inputViews;
  private Map<String, FormDataContentView> contentViews;
  
  public FormDataContentView(ObjectEditing viewModel, ObservableObject formDataContent, String path) {
    this.viewModel = viewModel;
    this.formDataContent = formDataContent;
    this.path = path;
    inputViews = new HashMap<>();
    contentViews = new HashMap<>();
    init();
  }

  /**
   * Method that initializes the UI and subscribes to the change listeners.
   */
  private void init() {
    formDataContent.onCollectionObjectChange(path, "values", this::onInputValuesChange);
    formDataContent.onCollectionObjectChange(path, "details", this::onDetailsChange);
  }
  
  private void onInputValuesChange(CollectionObjectChange changes) {
    // TODO
    for (ObjectChangeSimple change : changes.getChanges()) {
      String path = change.getPath();
      if (change.getOperation() == ChangeState.NEW) {
        FormInputView inputValueView = new FormInputView(viewModel, formDataContent, path);
        add(inputValueView);
        inputViews.put(path, inputValueView);
      }
    }
  }
  
  private void onDetailsChange(CollectionObjectChange changes) {
    // TODO
  }
  
}
