package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Function;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.HasItems;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.function.ValueProvider;

public class VaadinBinders {

  private VaadinBinders() {}

  // TODO find out if it's necessary at all
  // public static <S extends ObjectEditing> VaadinButtonBinder<S> bind(Button button, S editing,
  // Consumer<S> function) {
  // return new VaadinButtonBinder<>(button, editing, function);
  // }

  public static <WIDGET> VaadinHasValueBinder<WIDGET, WIDGET> bind(HasValue<?, WIDGET> field,
      ObservableObject observableObject, String... propertyPath) {
    return new VaadinHasValueBinder<>(field, observableObject, null, false, propertyPath);
  }

  public static <WIDGET> VaadinHasValueBinder<WIDGET, WIDGET> bindValue(HasValue<?, WIDGET> field,
      ObservableObject observableObject, Converter<WIDGET, WIDGET> converter,
      String... propertyPath) {
    return new VaadinHasValueBinder<>(field, observableObject, converter, false, propertyPath);
  }

  public static <WIDGET> VaadinHasValueBinder<WIDGET, WIDGET> bindValue(HasValue<?, WIDGET> field,
      ObservableObject observableObject, Converter<WIDGET, WIDGET> converter, boolean isRef,
      String... propertyPath) {
    return new VaadinHasValueBinder<>(field, observableObject, converter, isRef, propertyPath);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bindValue(
      HasValue<?, WIDGET> field, ObservableObject observableObject, Class<DATA> clazz,
      String... propertyPath) {
    return new VaadinHasValueBinder<>(field, observableObject, null, false, propertyPath);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bindValue(
      HasValue<?, WIDGET> field, ObservableObject observableObject, Class<DATA> clazz,
      Converter<WIDGET, DATA> converter, String... propertyPath) {
    return new VaadinHasValueBinder<>(field, observableObject, converter, false, propertyPath);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bindValue(
      HasValue<?, WIDGET> field, ObservableObject observableObject, Class<DATA> clazz,
      Converter<WIDGET, DATA> converter, boolean isRef, String... propertyPath) {
    return new VaadinHasValueBinder<>(field, observableObject, converter, isRef, propertyPath);
  }

  public static VaadinHasTextBinder bind(HasText label, ObservableObject observableObject,
      String... propertyPath) {
    return new VaadinHasTextBinder(label, observableObject, null, propertyPath);
  }

  public static VaadinHasTextBinder bindLabel(HasText label, ObservableObject observableObject,
      Function<Object, String> converter, String... propertyPath) {
    return new VaadinHasTextBinder(label, observableObject, converter, propertyPath);
  }

  public static <T> VaadinHasItemsBinder<T> bindItems(HasItems<T> grid, ObservableObject editing,
      String... collectionPath) {
    return new VaadinHasItemsBinder<>(grid, editing, v -> v, collectionPath);
  }

  public static <T> VaadinHasItemsBinder<T> bindItems(HasItems<T> grid,
      ObservableObject editing, ValueProvider<T, Object> idGetter, String... collectionPath) {
    return new VaadinHasItemsBinder<>(grid, editing, idGetter, collectionPath);
  }

  public static <C extends Component, T> VaadinHasValueBinder<T, T> bindSelection(
      SingleSelect<C, T> list,
      ObservableObject editing,
      String... collectionPath) {
    return new VaadinHasValueBinder<>(list, editing, null, true, collectionPath);
  }

  public static <C extends Component, T> VaadinHasValueBinder<T, T> bindSelection(
      SingleSelect<C, T> list,
      ObservableObject editing,
      Converter<T, T> converter, boolean isRef,
      String... collectionPath) {
    return new VaadinHasValueBinder<>(list, editing, converter, isRef, collectionPath);
  }

  public static <C extends Component, T> VaadinMultiSelectBinder<C, T> bindSelection(
      MultiSelect<C, T> list,
      ObservableObject editing,
      String... collectionPath) {
    return new VaadinMultiSelectBinder<>(list, editing, collectionPath);
  }

}
