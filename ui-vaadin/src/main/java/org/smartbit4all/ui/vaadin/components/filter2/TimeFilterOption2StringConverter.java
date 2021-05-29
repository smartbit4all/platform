package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.DateConverter;
import org.smartbit4all.api.filter.TimeFilterOption;
import com.google.common.base.Strings;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class TimeFilterOption2StringConverter implements Converter<TimeFilterOption, String> {

  @Override
  public Result<String> convertToModel(TimeFilterOption option, ValueContext context) {
    if (option == null) {
      return null;
    }
    return Result.ok(DateConverter.PREFIX_STRING + option.toString());
  }

  @Override
  public TimeFilterOption convertToPresentation(String value, ValueContext context) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    if (!value.startsWith(DateConverter.PREFIX_STRING)) {
      return null;
    }
    return TimeFilterOption.valueOf(value.substring(DateConverter.PREFIX_STRING.length()));
  }

}
