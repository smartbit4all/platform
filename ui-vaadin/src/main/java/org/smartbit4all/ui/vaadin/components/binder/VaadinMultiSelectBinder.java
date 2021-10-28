package org.smartbit4all.ui.vaadin.components.binder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.selection.MultiSelect;

public class VaadinMultiSelectBinder<C extends Component, T> extends VaadinCollectionBinder<T> {

  protected boolean propertyChangeProgress = false;

  private final MultiSelect<C, T> list;

  private ValueContext valueContext;

  private Validator<Set<T>> validator;

  private final String propertyPath;

  public VaadinMultiSelectBinder(MultiSelect<C, T> list, ObservableObject observableObject,
      String... collectionPath) {
    super(observableObject, collectionPath);
    this.list = list;
    propertyPath = PathUtility.concatPath(true, collectionPath);
    registerModelObserver();
    registerViewListener();
  }

  public void asRequired(String errorMessage) {
    list.setRequiredIndicatorVisible(true);
    validator = Validator.from(
        value -> {
          Set<T> data = list.getEmptyValue();
          return !Objects.equals(value, data);
        },
        errorMessage);
    initValueContext();
  }

  private void initValueContext() {
    if (valueContext != null) {
      return;
    }
    Locale locale = null;
    if (UI.getCurrent() != null) {
      locale = UI.getCurrent().getLocale();
    }
    if (locale == null) {
      locale = Locale.getDefault();
    }
    Component component = list instanceof Component ? (Component) list : null;
    this.valueContext = new ValueContext(component, list, locale);
  }

  public boolean validate() {
    if (validator != null) {
      Set<T> data = list.getValue();
      ValidationResult validationResult = validator.apply(data, valueContext);
      if (validationResult.isError()) {
        setFieldInvalid(validationResult.getErrorMessage());
        return false;
      }
      setFieldValid();
      return true;
    } else {
      return true;
    }
  }

  @Override
  protected void onCollectionObjectChanged(CollectionObjectChange changes) {
    propertyChangeProgress = true;
    super.onCollectionObjectChanged(changes);
    list.setValue(new LinkedHashSet<>(items));
    propertyChangeProgress = false;
  }

  protected void setModelState(Set<T> value) {
    if (!propertyChangeProgress) {
      observableObject.setValue(propertyPath, new ArrayList<>(value));
    }
  }

  protected void registerViewListener() {
    list.addValueChangeListener(event -> {
      Set<T> data = event.getValue();
      setModelState(data);
    });
  }

  private void setFieldInvalid(String errorMessage) {
    if (list instanceof HasValidation) {
      ((HasValidation) list).setInvalid(true);
      ((HasValidation) list).setErrorMessage(errorMessage);
    } // TODO else how to show error?
  }

  private void setFieldValid() {
    if (list instanceof HasValidation) {
      ((HasValidation) list).setInvalid(false);
    }
  }

}
