package org.smartbit4all.ui.vaadin.components.filter2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import org.smartbit4all.core.object.ObservableObject;

public class FilterOperationFactory {

  private static Map<String, BiFunction<ObservableObject, String, FilterOperationView>> operationViewFactoriesByCode =
      new HashMap<>();

  // add platform default operation views
  static {
    operationViewFactoriesByCode.put("filterop.txt.eq", FilterOperationTxtEqualsView::new);
    operationViewFactoriesByCode.put("filterop.txt.like", FilterOperationTxtLikeView::new);
    operationViewFactoriesByCode.put("filterop.number.eq", FilterOperationNumberEqualsView::new);
    operationViewFactoriesByCode.put("filterop.multi.eq", FilterOperationMultiSelectView::new);
    operationViewFactoriesByCode.put("filterop.combo.eq", FilterOperationComboBoxView::new);
    operationViewFactoriesByCode.put(
        "filterop.date.time.interval", FilterOperationDateTimeIntervalView::new);
    operationViewFactoriesByCode.put(
        "filterop.date.time.interval.cb", FilterOperationDateTimeComboBoxPickerView::new);
    operationViewFactoriesByCode.put(
        "filterop.date.time.eq", FilterOperationDateTimeEqualsView::new);
    operationViewFactoriesByCode.put(
        "filterop.date.interval", FilterOperationDateIntervalView::new);
    operationViewFactoriesByCode.put(
        "filterop.date.interval.cb", FilterOperationDateComboBoxPickerView::new);
    operationViewFactoriesByCode.put("filterop.date.eq", FilterOperationDateEqualsView::new);
  }

  private FilterOperationFactory() {
    // make in uninstantiable
  }

  public static void registerOperationViewFactory(String filterViewCode,
      BiFunction<ObservableObject, String, FilterOperationView> factory) {
    Objects.requireNonNull(factory, "FilterOperationView factory can not be null!");
    operationViewFactoriesByCode.put(filterViewCode, factory);
  }

  public static FilterOperationView create(String filterView, ObservableObject filterField,
      String path) {
    BiFunction<ObservableObject, String, FilterOperationView> factory =
        operationViewFactoriesByCode.get(filterView);
    if (factory == null) {
      throw new IllegalArgumentException("Invalid filterView parameter (" + filterView
          + ") in filter!");
    }
    return factory.apply(filterField, path);
  }


}
