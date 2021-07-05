package org.smartbit4all.ui.vaadin.components.form;

import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FormInputView extends FlexLayout {
  
  private ObjectEditing viewModel;
  private ObservableObject formInput;
  private String path;
  
  private FormInputOperationView operationView;
  
  public FormInputView(ObjectEditing viewModel, ObservableObject formInput, String path) {
    this.viewModel = viewModel;
    this.formInput = formInput;
    this.path = path;
    createUI();
  }

  /**
   * Method that initializes the UI and subscribes to the change listeners.
   */
  private void createUI() {
    
    
  }

}
