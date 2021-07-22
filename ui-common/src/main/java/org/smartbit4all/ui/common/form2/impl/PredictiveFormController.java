package org.smartbit4all.ui.common.form2.impl;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;

public interface PredictiveFormController {

  public void stepBack();
  
  public void loadRoot();
  
  public void save();

  public void selectWidget(WidgetDescriptor descriptor);

  public void loadAvailableWidgets();
  
}
