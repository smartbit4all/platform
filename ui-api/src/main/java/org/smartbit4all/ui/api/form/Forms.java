package org.smartbit4all.ui.api.form;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;

public class Forms {
  
  public static final WidgetInstance instanceOf(WidgetDescriptor widgetDescriptor) {
    return new WidgetInstance();
  }

}
