package org.smartbit4all.ui.vaadin.components.binder;

import java.util.Locale;
import java.util.Objects;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class VaadinHasValueBinder<WIDGET, DATA> extends VaadinAbstractBinder {

  protected ObservableObject observableObject;

  protected String propertyPath;

  protected boolean propertyChangeProgress = false;

  private HasValue<?, WIDGET> field;

  private Converter<WIDGET, DATA> converter;
  private ValueContext valueContext;

  private Validator<? super DATA> validator;

  public VaadinHasValueBinder(HasValue<?, WIDGET> field, ObservableObject observableObject,
      Converter<WIDGET, DATA> converter, boolean isRef, String... path) {
    super();
    this.field = field;
    this.observableObject = observableObject;
    this.propertyPath = PathUtility.concatPath(true, path);
    this.converter = converter;
    if (this.converter != null) {
      initValueContext();
    }

    if (isRef) {
      disposable = observableObject.onReferencedObjectChange(this::onReferenceObjectChanged, path);
    } else {
      disposable = observableObject.onPropertyChange(this::onPropertyChanged, path);
    }

    registerViewListener();
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
    Component component = field instanceof Component ? (Component) field : null;
    this.valueContext = new ValueContext(component, field, locale);
  }

  public void asRequired(String errorMessage) {
    field.setRequiredIndicatorVisible(true);
    validator = Validator.from(
        value -> {
          DATA data =
              getDataFromView(field.getEmptyValue()).getOrThrow(IllegalStateException::new);
          return !Objects.equals(value, data);
        },
        errorMessage);
    initValueContext();
  }

  public boolean validate() {
    if (validator != null) {
      Result<DATA> dataResult = getDataFromView(field.getValue());
      if (dataResult.isError()) {
        setFieldInvalid(dataResult.getMessage().orElse("Invalid value"));;
      }
      ValidationResult validationResult =
          validator.apply(dataResult.getOrThrow(IllegalStateException::new), valueContext);
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

  private void onPropertyChanged(PropertyChange value) {
    if (!propertyChangeProgress) {
      propertyChangeProgress = true;
      onModelChanged((DATA) value.getNewValue());
      propertyChangeProgress = false;
    }
  }

  private void onReferenceObjectChanged(ReferencedObjectChange value) {
    if (!propertyChangeProgress) {
      propertyChangeProgress = true;
      onModelChanged((DATA) value.getChange().getObject());
      propertyChangeProgress = false;
    }
  }

  protected void onModelChanged(DATA newData) {
    WIDGET newValue;
    if (converter != null) {
      newValue = converter.convertToPresentation(newData, valueContext);
    } else {
      newValue = (WIDGET) newData;
    }
    if (newValue == null) {
      if (field.isEmpty()) {
        return;
      }
      field.setValue(field.getEmptyValue());
    } else {
      field.setValue(newValue);
    }
  }

  protected void setModelState(DATA value) {
    if (!propertyChangeProgress) {
      observableObject.setValue(propertyPath, value);
    }
  }

  protected void registerViewListener() {
    field.addValueChangeListener(event -> {
      Result<DATA> result = getDataFromView(event.getValue());
      final DATA data;
      if (result.isError()) {
        setFieldInvalid(result.getMessage().orElse("Invalid value"));
        return;
      } else {
        if (field instanceof HasValidation) {
          ((HasValidation) field).setInvalid(false);
        }
        data = result.getOrThrow(IllegalStateException::new);
      }
      setModelState(data);
    });
  }

  private void setFieldInvalid(String errorMessage) {
    if (field instanceof HasValidation) {
      ((HasValidation) field).setInvalid(true);
      ((HasValidation) field).setErrorMessage(errorMessage);
    } // TODO else how to show error?
  }

  private void setFieldValid() {
    if (field instanceof HasValidation) {
      ((HasValidation) field).setInvalid(false);
    }
  }

  protected Result<DATA> getDataFromView(WIDGET value) {
    if (converter != null) {
      return converter.convertToModel(value, valueContext);
    } else {
      return Result.ok((DATA) value);
    }
  }
}
