package org.smartbit4all.ui.api.form;

import org.smartbit4all.ui.api.form.model.FormInputWidgetDescriptor;
import org.smartbit4all.ui.api.form.model.InputValue;

/**
 * API to control the predictive form, mainly used by the ViewModel of the predictive form MVVM
 * implementation.
 * 
 * @author Zsombor Nyilas
 *
 */
public interface FormApi {

  /**
   * Selects one of the widget descriptors from the available choices, and puts it up on the content with an empty value.
   * 
   * @param inputWidgetDescriptor the descriptor of the available choice
   */
  public void selectAvailableInput(FormInputWidgetDescriptor inputWidgetDescriptor);
  
  /**
   * Sets the value of the selected inputValue.
   * 
   * @param inputValue the inputValue, whose value is to be set
   * @param value the desired value
   */
  public void setValueOfInput(InputValue inputValue, String value);
  
  /**
   * Deselects an already selected InputValue
   * @param inputValue the InputValue to be removed from the content, and put back to the available choices
   */
  public void deselectContentInput(InputValue inputValue);
  
}
