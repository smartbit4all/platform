package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Consumer;
import java.util.function.Function;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.binder.HasItems;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.SingleSelect;

public class VaadinBinders {

  private VaadinBinders() {}

  public static <S extends ObjectEditing> VaadinButtonBinder<S> bind(Button button, S editing,
      Consumer<S> function) {
    return new VaadinButtonBinder<>(button, editing, function);
  }

  public static <WIDGET> VaadinHasValueBinder<WIDGET, WIDGET> bind(
      HasValue<?, WIDGET> field, ObservableObject observableObject,
      String path) {
    return new VaadinHasValueBinder<>(field, observableObject, path, null, false);
  }

  public static <WIDGET> VaadinHasValueBinder<WIDGET, WIDGET> bind(
      HasValue<?, WIDGET> field, ObservableObject observableObject,
      String path, Converter<WIDGET, WIDGET> converter) {
    return new VaadinHasValueBinder<>(field, observableObject, path, converter, false);
  }

  public static <WIDGET> VaadinHasValueBinder<WIDGET, WIDGET> bind(
      HasValue<?, WIDGET> field, ObservableObject observableObject,
      String path, Converter<WIDGET, WIDGET> converter, boolean isRef) {
    return new VaadinHasValueBinder<>(field, observableObject, path, converter, isRef);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bind(HasValue<?, WIDGET> field,
      ObservableObject observableObject, String path, Class<DATA> clazz) {
    return new VaadinHasValueBinder<>(field, observableObject, path, null, false);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bind(HasValue<?, WIDGET> field,
      ObservableObject observableObject, String path, Class<DATA> clazz,
      Converter<WIDGET, DATA> converter) {
    return new VaadinHasValueBinder<>(field, observableObject, path, converter, false);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bind(HasValue<?, WIDGET> field,
      ObservableObject observableObject, String path, Class<DATA> clazz,
      Converter<WIDGET, DATA> converter, boolean isRef) {
    return new VaadinHasValueBinder<>(field, observableObject, path, converter, isRef);
  }

  public static VaadinHasTextBinder bind(HasText label, ObservableObject observableObject,
      String path) {
    return new VaadinHasTextBinder(label, observableObject, path, null);
  }

  public static VaadinHasTextBinder bind(HasText label, ObservableObject observableObject,
      String path, Function<Object, String> converter) {
    return new VaadinHasTextBinder(label, observableObject, path, converter);
  }

  public static <T> VaadinHasItemsBinder<T> bind(HasItems<T> grid, ObservableObject editing,
      String path, String collectionName) {
    return new VaadinHasItemsBinder<>(grid, editing, path, collectionName);
  }

  public static <C extends Component, T> VaadinHasValueBinder<T, T> bindSelection(
      SingleSelect<C, T> list,
      ObservableObject editing,
      String path, String collectionName) {
    return new VaadinHasValueBinder<>(list, editing, path, collectionName, null, true);
  }

  public static <C extends Component, T> VaadinHasValueBinder<T, T> bindSelection(
      SingleSelect<C, T> list,
      ObservableObject editing,
      String path, String collectionName, Converter<T, T> converter, boolean isRef) {
    return new VaadinHasValueBinder<>(list, editing, path, collectionName, converter, isRef);
  }

  public static <C extends Component, T> VaadinMultiSelectBinder<C, T> bindSelection(
      MultiSelect<C, T> list,
      ObservableObject editing,
      String path, String collectionName) {
    return new VaadinMultiSelectBinder<>(list, editing, path, collectionName);
  }

}
