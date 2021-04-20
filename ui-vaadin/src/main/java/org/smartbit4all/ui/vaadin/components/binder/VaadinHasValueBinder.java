package org.smartbit4all.ui.vaadin.components.binder;

import java.util.Locale;
import java.util.Objects;
import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
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

public class VaadinHasValueBinder<WIDGET, DATA> {

  protected ObjectEditing editing;

  protected String path;

  protected boolean propertyChangeProgress = false;

  private HasValue<?, WIDGET> field;

  private Converter<WIDGET, DATA> converter;
  private ValueContext converterContext;

  private Validator<? super DATA> validator;

  public VaadinHasValueBinder(HasValue<?, WIDGET> field, ObjectEditing editing, String path) {
    super();
    this.field = field;
    this.editing = editing;
    this.path = path;

    subscribeToUIEvent();
    registerValueChangeListener();
  }

  // TODO make it like Binder.withConverter()
  public void setConverter(Converter<WIDGET, DATA> converter) {
    this.converter = converter;
    Locale locale = null;
    if (UI.getCurrent() != null) {
      locale = UI.getCurrent().getLocale();
    }
    if (locale == null) {
      locale = Locale.getDefault();
    }
    Component component;
    component = field instanceof Component ? (Component) field : null;
    this.converterContext = new ValueContext(component, field, locale);
  }

  public void asRequired(String errorMessage) {
    field.setRequiredIndicatorVisible(true);
    validator = Validator.from(
        value -> {
          DATA data =
              getDataFromWidget(field.getEmptyValue()).getOrThrow(IllegalStateException::new);
          return !Objects.equals(value, data);
        },
        errorMessage);
  }

  public boolean validate() {
    if (validator != null) {
      Result<DATA> dataResult = getDataFromWidget(field.getValue());
      if (dataResult.isError()) {
        setFieldInvalid(dataResult.getMessage().orElse("Invalid value"));;
      }
      ValidationResult validationResult =
          validator.apply(dataResult.getOrThrow(IllegalStateException::new), converterContext);
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

  protected void subscribeToUIEvent() {
    editing.publisher().properties().subscribe()
        .property(PathUtility.getParentPath(path), PathUtility.getLastPath(path))
        .add(this::onPropertyChanged);
  }

  private void onPropertyChanged(PropertyChange value) {
    propertyChangeProgress = true;
    onUIStateChanged(value);
    propertyChangeProgress = false;
  }

  protected void onUIStateChanged(PropertyChange value) {
    DATA newData = (DATA) value.getNewValue();
    WIDGET newValue;
    if (converter != null) {
      newValue = converter.convertToPresentation(newData, converterContext);
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

  protected void setUIState(DATA value) {
    if (!propertyChangeProgress) {
      editing.setValue(path, value);
    }
  }

  protected void registerValueChangeListener() {
    field.addValueChangeListener(event -> {
      Result<DATA> result = getDataFromWidget(event.getValue());
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
      setUIState(data);
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

  protected Result<DATA> getDataFromWidget(WIDGET value) {
    if (converter != null) {
      return converter.convertToModel(value, converterContext);
    } else {
      return Result.ok((DATA) value);
    }
  }
}
