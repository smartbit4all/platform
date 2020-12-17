package org.smartbit4all.ui.vaadin.components.form;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class DynamicForm<BEAN> extends Composite<FlexBoxLayout> {

  private Binder<BEAN> binder;

  public DynamicForm(Class<BEAN> beanClazz) {
    this.getContent().setSizeFull();
    this.getContent().setFlexWrap(FlexWrap.WRAP);
    init(beanClazz);
  }
  
  public void setBean(BEAN bean) {
    binder.setBean(bean);
  }

  private void init(Class<BEAN> beanClazz) {
    Map<String, BindingCandidate> bindings = new HashMap<>();
    for (int i = 0; i < beanClazz.getMethods().length; i++) {
      Method method = beanClazz.getMethods()[i];
      if (method.getDeclaringClass().equals(Object.class)) {
        continue;
      }
      String propertyCandidateName = getPropertyCandidateName(method);
      if (method.getParameters().length == 1
          && (method.getReturnType().equals(Void.TYPE)
              || method.getReturnType().equals(beanClazz))) {
        // This is a property setter method.
        BindingCandidate bindingCandidate = bindings.get(propertyCandidateName);
        if (bindingCandidate == null) {
          bindingCandidate = new BindingCandidate(null, method);
          bindings.put(propertyCandidateName, bindingCandidate);
        } else {
          bindingCandidate.setter = method;
        }
      } else if (method.getParameters().length == 0
          && !method.getReturnType().equals(Void.class)) {
        // This is the property getter method.
        BindingCandidate bindingCandidate = bindings.get(propertyCandidateName);
        if (bindingCandidate == null) {
          bindingCandidate = new BindingCandidate(method, null);
          bindings.put(propertyCandidateName, bindingCandidate);
        } else {
          bindingCandidate.getter = method;
        }
      }
    }
    
    binder = new Binder<>();
    for(Entry<String, BindingCandidate> bindingEntry : bindings.entrySet()) {
      BindingCandidate binding = bindingEntry.getValue();
      if(binding.getter.getReturnType().equals(String.class)) {
        String propertyName = bindingEntry.getKey();
        TextField textField = new TextField(propertyName);
  //      binder.bind(textField, propertyName);
        binder.bind(textField, bean -> invokeGetter(binding, bean), (bean, value) -> invokeSetter(binding, bean, value));
        this.getContent().add(textField);
      }
    }
    
  }
  
  private <T> T invokeGetter(BindingCandidate binding, BEAN bean) {
    try {
      return (T) binding.getter.invoke(bean);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  private <T> void invokeSetter(BindingCandidate binding, BEAN bean, T value) {
    try {
      binding.setter.invoke(bean, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  private String getPropertyCandidateName(Method method) {
//    // if the method is annotated, the annotation describes the property
//    if (method.isAnnotationPresent(PropertyAccessor.class)) {
//      PropertyAccessor propertyAccessor = method.getAnnotation(PropertyAccessor.class);
//      return propertyAccessor.value();
//    }
//    // the name of the method may match the target property
    
    String name = method.getName();
    if(name.startsWith("get") || name.startsWith("set")) {
      name = name.substring(3);
    }
    return name;
  }
  
  static class BindingCandidate {

    Method getter;

    Method setter;

    public BindingCandidate(Method getter, Method setter) {
      super();
      this.getter = getter;
      this.setter = setter;
    }

  }
  
}
