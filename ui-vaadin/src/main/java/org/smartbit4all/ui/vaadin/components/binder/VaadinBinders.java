package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.smartbit4all.api.object.ObjectEditing;
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
      HasValue<?, WIDGET> field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static <WIDGET, DATA> VaadinHasValueBinder<WIDGET, DATA> bind(HasValue<?, WIDGET> field,
      ObjectEditing editing, String path, Class<DATA> clazz) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static VaadinHasTextBinder bind(HasText label, ObjectEditing editing, String path) {
    return new VaadinHasTextBinder(label, editing, path);
  }

  public static <T, E extends ObjectEditing> VaadinGridBinder<T, E> bind(Grid<T> grid, E editing,
      String path,
      String collectionName,
      BiFunction<E, String, T> itemGetter) {
    return new VaadinGridBinder<>(grid, editing, path, collectionName, itemGetter);
  }
}
