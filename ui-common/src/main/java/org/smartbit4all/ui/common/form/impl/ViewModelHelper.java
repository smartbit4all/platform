package org.smartbit4all.ui.common.form.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.form.model.DetailWidgetDescriptor;
import org.smartbit4all.ui.api.form.model.EntityDescriptor;
import org.smartbit4all.ui.api.form.model.FixedLayoutFormInstance;
import org.smartbit4all.ui.api.form.model.FormDataContent;
import org.smartbit4all.ui.api.form.model.FormDataDetailContent;
import org.smartbit4all.ui.api.form.model.InputValue;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphDescriptor;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.PropertyWidgetDescriptor;

public class ViewModelHelper {

  private static Map<Class<?>, ApiBeanDescriptor> formApiBeans;

  static {
    Set<Class<?>> beans = new HashSet<>();
    beans.add(PredictiveFormInstance.class);
    beans.add(FixedLayoutFormInstance.class);
    beans.add(FormDataContent.class);
    beans.add(FormDataDetailContent.class);
    beans.add(InputValue.class);
    beans.add(EntityDescriptor.class);
    beans.add(InputValue.class);
    beans.add(PropertyWidgetDescriptor.class);
    beans.add(DetailWidgetDescriptor.class);
    beans.add(PredictiveInputGraphDescriptor.class);
    beans.add(PredictiveInputGraphNode.class);
    beans.add(Value.class);
    formApiBeans = ApiBeanDescriptor.of(beans);

  }

  private ViewModelHelper() {}

  static Map<Class<?>, ApiBeanDescriptor> getFormApiBeans() {
    return formApiBeans;
  }

}
