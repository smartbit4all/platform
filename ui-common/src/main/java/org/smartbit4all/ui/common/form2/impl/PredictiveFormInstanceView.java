package org.smartbit4all.ui.common.form2.impl;

import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.api.form.model.WidgetType;

public interface PredictiveFormInstanceView {
  
  /**
   * Renders both the available and the visible widgets on the UI.
   */
  public void renderWidgets();
  
  public void openValueDialog(WidgetType widgetType, WidgetInstance instance, WidgetDescriptor widgetDescriptor);
  
  public void navigateTo(EntityFormInstance instance);
  
//  public void renderAvailableWidget(String label, String icon, URI descriptorUri);
  
//  public void renderVisibleWidget(String label, String icon, URI)

}
