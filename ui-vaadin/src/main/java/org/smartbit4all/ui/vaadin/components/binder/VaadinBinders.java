package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Consumer;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;

public class VaadinBinders {

  private VaadinBinders() {}

  public static <S extends ObjectEditing> VaadinButtonBinder<S> bind(Button button, S editing,
      Consumer<S> function) {
    return new VaadinButtonBinder<>(button, editing, function);
  }

  public static <WIDGET> VaadinHasValueBinder<WIDGET, WIDGET> bind(
      HasValue<?, WIDGET> field, ObservableObject observableObject,
      String path) {
    return new VaadinHasValueBinder<>(field, observableObject, path);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bind(HasValue<?, WIDGET> field,
      ObservableObject observableObject, String path, Class<DATA> clazz) {
    return new VaadinHasValueBinder<>(field, observableObject, path);
  }

  public static VaadinHasTextBinder bind(HasText label, ObservableObject observableObject,
      String path) {
    return new VaadinHasTextBinder(label, observableObject, path);
  }

  public static <T> VaadinGridBinder<T> bind(Grid<T> grid, ObservableObject editing, String path,
      String collectionName) {
    return new VaadinGridBinder<>(grid, editing, path, collectionName);
  }
}
