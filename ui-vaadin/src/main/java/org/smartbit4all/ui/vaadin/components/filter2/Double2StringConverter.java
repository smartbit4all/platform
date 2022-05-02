package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.util.FilterConfigs;
import com.google.common.base.Strings;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class Double2StringConverter implements Converter<Double, String> {

  @Override
  public Result<String> convertToModel(Double option, ValueContext context) {
    if (option == null) {
      return Result.ok(null);
    }
    return Result.ok(FilterConfigs.PREFIX_DOUBLE + option.toString());
  }

  @Override
  public Double convertToPresentation(String value, ValueContext context) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    if (!value.startsWith(FilterConfigs.PREFIX_DOUBLE)) {
      return null;
    }
    return Double.valueOf(value.substring(FilterConfigs.PREFIX_DOUBLE.length()));
  }
}
