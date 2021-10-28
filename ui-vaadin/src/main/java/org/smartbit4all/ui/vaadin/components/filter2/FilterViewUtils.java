package org.smartbit4all.ui.vaadin.components.filter2;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.smartbit4all.api.filter.DateConverter;
import org.smartbit4all.api.filter.TimeFilterOption;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import org.smartbit4all.ui.vaadin.localization.ComponentLocalizations;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.shared.Registration;

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
    dateTime.setMax(LocalDate.now().plusDays(1l).atStartOfDay().minusMinutes(1l));
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

  public static VaadinHasValueBinder<LocalDate, String> bindDate(DatePicker date,
      ObservableObject filterField, String path,
      int number) {
    VaadinHasValueBinder<LocalDate, String> binder = VaadinBinders.bindValue(date, filterField,
        String.class, new LocalDate2StringConverter(), path, "value" + number);
    return binder;
  }

  public static VaadinHasValueBinder<LocalDateTime, String> bindDateTime(DateTimePicker dateTime,
      ObservableObject filterField,
      String path, int number) {
    VaadinHasValueBinder<LocalDateTime, String> binder = VaadinBinders.bindValue(dateTime,
        filterField, String.class, new LocalDateTime2StringConverter(), path, "value" + number);
    return binder;
  }

  public static VaadinHasValueBinder<TimeFilterOption, String> bindTimeFilterOptionCombo(
      ComboBox<TimeFilterOption> combo,
      ObservableObject filterField, String path, int number) {
    VaadinHasValueBinder<TimeFilterOption, String> binder = VaadinBinders.bindValue(combo,
        filterField, String.class, new TimeFilterOption2StringConverter(), path, "value" + number);
    return binder;
  }

  public static VaadinHasValueBinder<Integer, String> bindInteger(
      IntegerField integerField,
      ObservableObject filterField, String path, int number) {
    VaadinHasValueBinder<Integer, String> binder = VaadinBinders.bindValue(integerField,
        filterField, String.class, new Integer2StringConverter(), path, "value" + number);
    return binder;
  }

  // TODO this is a hotfix -> should use viewModel!!!
  public static Registration handleTimeFilterValueChange(DateTimePicker picker,
      ComboBox<TimeFilterOption> combo) {

    Registration listener = picker.addValueChangeListener(e -> {
      if (e.isFromClient()) {
        combo.setValue(TimeFilterOption.OTHER);
      }
    });
    return listener;
  }

  public static Registration handleDateFilterValueChange(DatePicker picker,
      ComboBox<TimeFilterOption> combo) {

    Registration listener = picker.addValueChangeListener(e -> {
      if (e.isFromClient()) {
        combo.setValue(TimeFilterOption.OTHER);
      }
    });
    return listener;
  }

  // TODO move to viewModel, maybe FilterFieldViewModel or FilterGroupViewModel?
  public static Registration handleTimeFilterOptionComboChange(ComboBox<TimeFilterOption> combo,
      ObservableObject filterField, String path, boolean isDateTime) {
    String valuePath = PathUtility.concatPath(path, "value");

    Registration listener = combo.addValueChangeListener(e -> {
      LocalTime now = LocalTime.now();
      LocalDate today = LocalDate.now();
      LocalTime startTime = LocalTime.of(0, 0);
      LocalTime endTime = LocalTime.of(23, 59, 59);
      LocalDate startDate;
      LocalDate endDate;
      TimeFilterOption timeFilterOption = combo.getValue();
      if (timeFilterOption == null) {
        return;
      }
      switch (timeFilterOption) {
        case LAST_MONTH:
          startDate = today.with(DAY_OF_MONTH, 1).minus(1, MONTHS);
          endDate = today.with(DAY_OF_MONTH, 1).minusDays(1);
          break;
        case LAST_WEEK:
          startDate = today.with(DayOfWeek.MONDAY).minus(1, WEEKS);
          endDate = today.with(DayOfWeek.MONDAY).minusDays(1);
          break;
        case THIS_MONTH:
          startDate = today.with(DAY_OF_MONTH, 1);
          endDate = today;
          endTime = now;
          break;
        case TODAY:
          startDate = today;
          endDate = today;
          endTime = now;
          break;
        case YESTERDAY:
          startDate = today.minusDays(1);
          endDate = today.minusDays(1);
          break;
        case THIS_YEAR:
          startDate = today.with(DAY_OF_YEAR, 1);
          endDate = today;
          endTime = now;
          break;
        case LAST_FIVE_YEARS:
          startDate = today.minusYears(5);
          endDate = today;
          endTime = now;
          break;
        case OTHER:
        default:
          startDate = null;
          endDate = null;
          break;
      }
      if (isDateTime) {
        if (startDate != null) {
          String start = LocalDateTime.of(startDate, startTime).toString();
          filterField.setValue(valuePath + "1", DateConverter.PREFIX_DATETIME + start);
        }
        if (endDate != null) {
          String end = LocalDateTime.of(endDate, endTime).toString();
          filterField.setValue(valuePath + "2", DateConverter.PREFIX_DATETIME + end);
        }
      } else {
        if (startDate != null) {
          String start = startDate.toString();
          filterField.setValue(valuePath + "1", DateConverter.PREFIX_DATE + start);
        }
        if (endDate != null) {
          String end = endDate.toString();
          filterField.setValue(valuePath + "2", DateConverter.PREFIX_DATE + end);
        }
      }
    });
    return listener;
  }

}
