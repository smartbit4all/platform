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

  public static VaadinHasValueBinder<String, String> bind(TextField field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static <DATA> VaadinHasValueBinder<String, DATA> bind(TextField field,
      ObjectEditing editing, String path, Class<DATA> clazz) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static VaadinHasValueBinder<Double, Double> bind(NumberField field, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static VaadinHasValueBinder<Integer, Integer> bind(IntegerField field,
      ObjectEditing editing, String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static VaadinHasValueBinder<BigDecimal, BigDecimal> bind(BigDecimalField field,
      ObjectEditing editing, String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static VaadinHasValueBinder<LocalDate, LocalDate> bind(DatePicker field,
      ObjectEditing editing, String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static VaadinHasValueBinder<LocalDateTime, LocalDateTime> bind(DateTimePicker field,
      ObjectEditing editing, String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static VaadinHasValueBinder<LocalTime, LocalTime> bind(TimePicker field,
      ObjectEditing editing, String path) {
    return new VaadinHasValueBinder<>(field, editing, path);
  }

  public static <T> VaadinHasValueBinder<T, T> bind(ComboBox<T> comboBox, ObjectEditing editing,
      String path) {
    return new VaadinHasValueBinder<>(comboBox, editing, path);
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
