package org.smartbit4all.ui.api.stateful;

import java.util.Map;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.viewmodel.ObjectEditingImpl;

public class StatefulBeanEditingImpl extends ObjectEditingImpl implements StatefulBeanEditing {

  protected ObservableObjectImpl observableBean;

  StatefulBean bean;

  StatefulBean wrapper;

  Map<Class<?>, ApiBeanDescriptor> descriptors;

  public StatefulBeanEditingImpl(Map<Class<?>, ApiBeanDescriptor> descriptors) {
    super();

    this.descriptors = descriptors;
    observableBean = new ObservableObjectImpl();
  }

  @Override
  public ObservableObject bean() {
    return observableBean;
  }

  @Override
  public void setBean(StatefulBean bean) {
    this.bean = bean;

    ref = new ApiObjectRef(null, bean, descriptors);
    wrapper = ref.getWrapper(StatefulBean.class);
    observableBean.setRef(ref);
  }

  @Override
  public void setValue(String name) {
    wrapper.setValue(name);
  }

  @Override
  public void setValueAndNotifyAll(String name) {
    wrapper.setValue(name);
  }

  @Override
  public void setValueAndNotify(String name) {
    wrapper.setValue(name);
  }

  @Override
  public void setValueAndNotifyOther(String name) {
    wrapper.setValue(name);
  }

}
