package org.smartbit4all.ui.vaadin.components.filter2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.smartbit4all.api.filter.TimeFilterOption;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.localization.ComponentLocalizations;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

class FilterViewUtils {

  private FilterViewUtils() {}

  public static DatePicker createDatePicker() {
    DatePicker date = new DatePicker();
    date.setMax(LocalDate.now());
    ComponentLocalizations.localize(date);
    return date;
  }

  public static DateTimePicker createDateTimePicker() {
    DateTimePicker dateTime = new DateTimePicker();
    dateTime.setMax(LocalDateTime.now());
    ComponentLocalizations.localize(dateTime);
    return dateTime;
  }

  public static ComboBox<TimeFilterOption> createTimeFilterOptionCombo(Component parent) {
    ComboBox<TimeFilterOption> combo = new ComboBox<>();
    combo.setItems(TimeFilterOption.values());
    combo.setItemLabelGenerator(option -> parent.getTranslation(option.getLabel()));
    combo.setRequired(true);
    return combo;
  }

  public static void bindDate(DatePicker date, ObservableObject filterField, String path,
      String property) {
    String typePath = PathUtility.concatPath(path, property + "/type");
    String valuePath = PathUtility.concatPath(path, property + "/value");
    VaadinBinders.bind(date, filterField, valuePath, String.class)
        .setConverter(new LocalDate2StringConverter());
    date.addValueChangeListener(
        e -> filterField.setValue(typePath, LocalDate.class.getName()));
  }

  public static void bindDateTime(DateTimePicker dateTime, ObservableObject filterField,
      String path, String property) {
    String typePath = PathUtility.concatPath(path, property + "/type");
    String valuePath = PathUtility.concatPath(path, property + "/value");
    VaadinBinders.bind(dateTime, filterField, valuePath, String.class)
        .setConverter(new LocalDateTime2StringConverter());
    dateTime.addValueChangeListener(
        e -> filterField.setValue(typePath, LocalDateTime.class.getName()));
  }

  public static void bindTimeFilterOptionCombo(ComboBox<TimeFilterOption> combo,
      ObservableObject filterField, String path,
      String property) {
    String typePath = PathUtility.concatPath(path, property + "/type");
    String valuePath = PathUtility.concatPath(path, property + "/value");
    VaadinBinders.bind(combo, filterField, valuePath, String.class)
        .setConverter(new TimeFilterOption2StringConverter());
    combo.addValueChangeListener(
        e -> filterField.setValue(typePath, String.class.getName()));

  }
}
