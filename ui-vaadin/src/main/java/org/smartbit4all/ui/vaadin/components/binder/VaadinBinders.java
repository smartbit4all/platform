package org.smartbit4all.ui.vaadin.components.binder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.smartbit4all.api.object.ObjectEditing;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;

public class VaadinBinders {

  private VaadinBinders() {}

  public static <S extends ObjectEditing> VaadinButtonBinder<S> bind(Button button, S editing,
      Consumer<S> function) {
    return new VaadinButtonBinder<>(button, editing, function);
  }

  public static VaadinHasValueBinder<String> bind(TextField field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path, String.class);
  }

  public static VaadinHasValueBinder<Double> bind(NumberField field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path, Double.class);
  }

  public static VaadinHasValueBinder<Integer> bind(IntegerField field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path, Integer.class);
  }

  public static VaadinHasValueBinder<BigDecimal> bind(BigDecimalField field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path, BigDecimal.class);
  }

  public static VaadinHasValueBinder<LocalDate> bind(DatePicker field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path, LocalDate.class);
  }

  public static VaadinHasValueBinder<LocalDateTime> bind(DateTimePicker field,
      ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path, LocalDateTime.class);
  }

  public static VaadinHasValueBinder<LocalTime> bind(TimePicker field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path, LocalTime.class);
  }

  public static <T> VaadinHasValueBinder<T> bind(ComboBox<T> comboBox,
      ObjectEditing editing,
      String path, Class<T> valueClass) {
    return new VaadinHasValueBinder<>(comboBox, editing, path, valueClass);
  }

  public static void bind(HasText label, ObjectEditing editing,
      String path) {
    new VaadinHasTextBinder(label, editing, path);
  }

  public static <T, E extends ObjectEditing> VaadinGridBinder<T, E> bind(Grid<T> grid, E editing,
      String path,
      String collectionName,
      BiFunction<E, String, T> itemGetter) {
    return new VaadinGridBinder<>(grid, editing, path, collectionName, itemGetter);
  }
}
