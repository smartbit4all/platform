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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.vaadin.localization.TranslationUtil;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class DynamicForm<BEAN> extends Composite<FlexLayout> {

  private static final Logger log = LoggerFactory.getLogger(DynamicForm.class);
  
  private Binder<BEAN> binder;
  
  Map<String, Component> componentsByPropertyName = new LinkedHashMap<>();

  public DynamicForm() {

  }

  public DynamicForm(Class<BEAN> beanClazz) {
    layoutInit(beanClazz);
    initBean(beanClazz);
  }
  
  public DynamicForm(Class<BEAN> beanClazz, ArrayList<String> orderedPropertyNames) {
    layoutInit(beanClazz);
    initBean(beanClazz, orderedPropertyNames);
  }

  protected void layoutInit(Class<BEAN> beanClazz) {
    this.getContent().setWidthFull();
    this.getContent().setFlexWrap(FlexWrap.WRAP);
    this.getContent().addClassName("dynaform");
    this.getContent().addClassName(toKebabCase(beanClazz.getSimpleName()));
  }

  public void setBean(BEAN bean) {
    binder.setBean(bean);
  }

  public void setReadOnly(boolean readOnly) {
    binder.setReadOnly(readOnly);
  }
  
  protected void initBean(Class<BEAN> beanClazz, ArrayList<String> orderedPropertyNames)  {
    for (String propertyName : orderedPropertyNames) {
      componentsByPropertyName.put(propertyName, null);
    }
    initBean(beanClazz);
  }

  protected void initBean(Class<BEAN> beanClazz) {
    Map<String, BindingCandidate> bindings = collectBindings(beanClazz);
    binder = new Binder<>();
    for (Entry<String, BindingCandidate> bindingEntry : bindings.entrySet()) {
      BindingCandidate binding = bindingEntry.getValue();
      if (binding.isComplete()) {
        String propertyName = bindingEntry.getKey();
        String label = getLabel(propertyName);
        AbstractField<?, ?> field = null;

        // TODO refactor the underlying if blocks to handle types from a static map
        if (isGetterReturnMatches(binding, String.class)) {
          field = new TextField(label);
          binder.bind(field, bean -> invokeGetter(binding, bean, null),
              (bean, value) -> invokeSetter(binding, bean, value, null));
        }
        if (isGetterReturnMatches(binding, Integer.class)) {
          field = new IntegerField(label);
          binder.bind((IntegerField) field,
              bean -> invokeGetter(binding, bean, null),
              (bean, value) -> invokeSetter(binding, bean, value, null));
        }
        if (isGetterReturnMatches(binding, Long.class)) {
          field = new BigDecimalField(label);
          BinderValueConverter<Long, BigDecimal> converter = createConverter(
              bigdec -> bigdec.longValue(),
              longValue -> longValue == null ? null : BigDecimal.valueOf(longValue));
          binder.bind((BigDecimalField) field,
              bean -> invokeGetter(binding, bean, converter),
              (bean, value) -> invokeSetter(binding, bean, value, converter));
        }
        if (isGetterReturnMatches(binding, LocalDate.class)) {
          field = new DatePicker(label);
          binder.bind((DatePicker) field,
              bean -> invokeGetter(binding, bean, null),
              (bean, value) -> invokeSetter(binding, bean, value, null));
        }
        if (isGetterReturnMatches(binding, LocalDateTime.class)) {
          field = new DateTimePicker(label);
          binder.bind((DateTimePicker) field,
              bean -> invokeGetter(binding, bean, null),
              (bean, value) -> invokeSetter(binding, bean, value, null));
        }
        if (isGetterReturnMatches(binding, Boolean.class)) {
          field = new Checkbox(label);
          binder.bind((Checkbox) field,
              bean -> invokeGetter(binding, bean, null),
              (bean, value) -> invokeSetter(binding, bean, value, null));
        }

        if(field != null) {
          if (field instanceof HasStyle) {
            String className = propertyName.substring(propertyName.lastIndexOf(".") + 1);
            ((HasStyle) field).setClassName(toKebabCase(className));
          }
          componentsByPropertyName.put(label, field);
        } else {
          log.debug("There was no field created for property '" + propertyName + "' due its unhandled type!");
        }
      }
    }
    addComponentsToContent(componentsByPropertyName.values().stream().collect(Collectors.toList()));

  }

  protected void addComponentsToContent(List<Component> componentList) {
    for (Component component : componentList) {
      this.getContent().add(component);
    }
  }

  private String toKebabCase(String str) {
    return str.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
  }

  private <BEANTYPE, BINDERTYPE> BinderValueConverter<BEANTYPE, BINDERTYPE> createConverter(
      Function<BINDERTYPE, BEANTYPE> binderToBean, Function<BEANTYPE, BINDERTYPE> beanToBinder) {
    return new BinderValueConverter<BEANTYPE, BINDERTYPE>() {

      @Override
      public BEANTYPE binderToBean(BINDERTYPE valueToSet) {
        return binderToBean.apply(valueToSet);
      }

      @Override
      public BINDERTYPE beanToBinder(BEANTYPE valueFromBean) {
        return beanToBinder.apply(valueFromBean);
      }

    };
  }

  private static interface BinderValueConverter<BEANTYPE, BINDERTYPE> {
    public BEANTYPE binderToBean(BINDERTYPE valueToSet);

    public BINDERTYPE beanToBinder(BEANTYPE valueFromBean);
  }

  private boolean isGetterReturnMatches(BindingCandidate binding, Class<?>... classesToCheck) {
    Objects.requireNonNull(classesToCheck);
    Class<?> returnType = binding.getter.getReturnType();
    for (Class<?> classToCheck : classesToCheck) {
      if (returnType.equals(classToCheck)) {
        return true;
      }
    }
    return false;
  }

  private Map<String, BindingCandidate> collectBindings(Class<BEAN> beanClazz) {
    Map<String, BindingCandidate> bindings = new TreeMap<>();
    Method[] methods = beanClazz.getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      if (method.getDeclaringClass().equals(Object.class)) {
        continue;
      }
      String propertyCandidateName = getPropertyName(method);
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
    return bindings;
  }

  private <BINDERTYPE, BEANTYPE> BINDERTYPE invokeGetter(BindingCandidate binding, BEAN bean,
      BinderValueConverter<BEANTYPE, BINDERTYPE> converter) {
    try {
      Object getterResult = binding.getter.invoke(bean);
      if (converter == null) {
        return (BINDERTYPE) getterResult;
      } else {
        return converter.beanToBinder((BEANTYPE) getterResult);
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private <BINDERTYPE, BEANTYPE> void invokeSetter(BindingCandidate binding, BEAN bean,
      BINDERTYPE value,
      BinderValueConverter<BEANTYPE, BINDERTYPE> converter) {
    try {
      BEANTYPE beanValue = converter == null ? (BEANTYPE) value : converter.binderToBean(value);
      binding.setter.invoke(bean, beanValue);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private String getPropertyName(Method method) {
    String name = method.getName();
    if (name.startsWith("get") || name.startsWith("set")) {
      // cut the first three letters and lowercase the new first one.
      name = name.substring(3, 4).toLowerCase() + name.substring(4);
    }
    return method.getDeclaringClass().getName() + "." + name;
  }

  private String getLabel(String propertyName) {
    String possibleTranslation = TranslationUtil.INSTANCE().getPossibleTranslation(propertyName);
    if (propertyName == possibleTranslation) {
      return propertyName.substring(propertyName.lastIndexOf(".") + 1);
    }
    return possibleTranslation;
  }

  static class BindingCandidate {

    Method getter;

    Method setter;

    public BindingCandidate(Method getter, Method setter) {
      super();
      this.getter = getter;
      this.setter = setter;
    }

    public boolean isComplete() {
      return getter != null && setter != null;
    }

  }

}
