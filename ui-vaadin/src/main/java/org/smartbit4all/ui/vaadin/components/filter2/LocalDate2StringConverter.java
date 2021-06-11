package org.smartbit4all.ui.vaadin.components.filter2;

import java.time.LocalDate;
import org.smartbit4all.api.filter.DateConverter;
import com.google.common.base.Strings;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class LocalDate2StringConverter implements Converter<LocalDate, String> {

  @Override
  public Result<String> convertToModel(LocalDate date, ValueContext context) {
    if (date == null) {
      return Result.ok(null);
    }

    return Result.ok(DateConverter.PREFIX_DATE + date.toString());
  }

  @Override
  public LocalDate convertToPresentation(String value, ValueContext context) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    return DateConverter.getDateWithType(value);
  }

}
