package org.smartbit4all.ui.common.form.impl;

import java.net.URI;
import org.smartbit4all.ui.api.form.FormApi;
import org.smartbit4all.ui.api.form.model.FormDataContent;
import org.smartbit4all.ui.api.form.model.InputValue;
import org.smartbit4all.ui.api.form.model.PropertyWidgetDescriptor;

public class FormApiImpl implements FormApi {

  @Override
  public void selectAvailableInput(FormDataContent content,
      PropertyWidgetDescriptor inputWidgetDescriptor) {
    for (URI uri : inputWidgetDescriptor.getUris()) {
      InputValue inputValue = new InputValue();
      inputValue.setPropertyUri(uri);
      content.addValuesItem(inputValue);
    }
  }

  @Override
  public void setValueOfInput(InputValue inputValue, String value) {
    // TODO should the conversion be here?
    inputValue.setPropertyValue(value);
  }

  @Override
  public void deselectContentInput(FormDataContent content, InputValue inputValue) {
    content.getValues().remove(inputValue);
  }

}
