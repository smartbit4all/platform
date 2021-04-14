package org.smartbit4all.ui.vaadin.components.binder;

import java.util.Locale;
import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class VaadinHasValueBinder<WIDGET, DATA> {

  protected ObjectEditing editing;

  protected String path;

  protected boolean propertyChangeProgress = false;

  private HasValue<?, WIDGET> field;

  private Converter<WIDGET, DATA> converter;
  private ValueContext converterContext;

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
      if (converter != null) {
        Result<DATA> result = converter.convertToModel(event.getValue(), converterContext);
        result.ifError(error -> {
          if (field instanceof HasValidation) {
            ((HasValidation) field).setInvalid(true);
            ((HasValidation) field).setErrorMessage(error);
          }
        });
        result.ifOk(data -> setUIState(data));
        if (result.isError()) {
          if (field instanceof HasValidation) {
            ((HasValidation) field).setInvalid(true);
            ((HasValidation) field).setErrorMessage(result.getMessage().orElse("Invalid value"));
          } else {
            // TODO how to show error?
          }
        }
      } else {
        setUIState((DATA) event.getValue());
      }
    });
  }
}
