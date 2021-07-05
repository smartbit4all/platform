package org.smartbit4all.ui.common.form.impl;

import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.form.PredictiveFormViewModel;
import org.smartbit4all.ui.api.form.model.InputValue;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.PropertyWidgetDescriptor;

/**
 * The implementation of the ViewModel for the whole view. This connects the view and the model in
 * the MVVM architecture.
 * 
 * @author Zsombor Nyilas
 *
 */
public class PredictiveFormViewModelImpl extends ObjectEditingImpl
    implements PredictiveFormViewModel {

  private ObservableObjectImpl predictiveFormModelObservable;
  
  private PredictiveFormInstance predictiveFormModel;
  
  public PredictiveFormViewModelImpl() {
   PredictiveFormInstance model = new PredictiveFormInstance();
   ref = new ApiObjectRef(null, model, ViewModelHelper.getFormApiBeans());
   predictiveFormModelObservable.setRef(ref);
   this.predictiveFormModel = ref.getWrapper(PredictiveFormInstance.class);
  }

  @Override
  public ObservableObject model() {
    return predictiveFormModelObservable;
  }

  @Override
  public void initModel(String uri) {
    // TODO initialize a model that can be used for testing purposes, uri based implementation comes later
    
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    // TODO Auto-generated method stub
    switch (command) {
      case "CREATE_FRAGMENT":
        // TODO add to FormDataContent.getValues()
        InputValue inputValue = createInputValueFromDescriptorPath(commandPath);
        predictiveFormModel.getContent().addValuesItem(inputValue);
        break;
      case "SET_VALUE":
        // TODO
        break;
      default:
        break;
    }
  }

  // private FormInputWidgetDescriptor createAvailableInputFromDescriptorPath(String path) {
  // ApiObjectRef availableInputRef = ref.getValueRefByPath(path);
  // FormInputWidgetDescriptor descriptor =
  // availableInputRef.getWrapper(FormInputWidgetDescriptor.class);
  // return createAvailableInputFromDescriptor(descriptor);
  // }

  private InputValue createInputValueFromDescriptorPath(String descriptorPath) {
    ApiObjectRef availableInputRef = ref.getValueRefByPath(descriptorPath);
    PropertyWidgetDescriptor descriptor =
        availableInputRef.getWrapper(PropertyWidgetDescriptor.class);
    // return createAvailableInputFromDescriptor(descriptor);
    InputValue inputValue = new InputValue();
    inputValue.setPropertyUri(descriptor.getUris().get(0));
    return inputValue;
  }

  // private datacucc createAvailableInputFromDescriptor(
  // FormInputWidgetDescriptor descriptor) {
  // // TODO Auto-generated method stub
  // return null;
  // }

}
