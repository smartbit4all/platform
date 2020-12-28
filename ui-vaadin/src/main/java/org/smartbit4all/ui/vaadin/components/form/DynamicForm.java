/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.form;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class DynamicForm<BEAN> extends Composite<FlexLayout> {

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
    for (Entry<String, BindingCandidate> bindingEntry : bindings.entrySet()) {
      BindingCandidate binding = bindingEntry.getValue();
      if (binding.getter.getReturnType().equals(String.class)) {
        String propertyName = bindingEntry.getKey();
        TextField textField = new TextField(propertyName);
        // binder.bind(textField, propertyName);
        binder.bind(textField, bean -> invokeGetter(binding, bean),
            (bean, value) -> invokeSetter(binding, bean, value));
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
    // // if the method is annotated, the annotation describes the property
    // if (method.isAnnotationPresent(PropertyAccessor.class)) {
    // PropertyAccessor propertyAccessor = method.getAnnotation(PropertyAccessor.class);
    // return propertyAccessor.value();
    // }
    // // the name of the method may match the target property

    String name = method.getName();
    if (name.startsWith("get") || name.startsWith("set")) {
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
