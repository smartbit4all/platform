package org.smartbit4all.ui.vaadin.components.filter2;

import java.time.LocalDateTime;
import org.smartbit4all.api.filter.DateConverter;
import org.smartbit4all.api.filter.util.FilterConfigs;
import com.google.common.base.Strings;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class LocalDateTime2StringConverter implements Converter<LocalDateTime, String> {

  @Override
  public Result<String> convertToModel(LocalDateTime datetime, ValueContext context) {
    if (datetime == null) {
      return Result.ok(null);
    }
    return Result.ok(FilterConfigs.PREFIX_DATETIME + datetime.toString());
  }

  @Override
  public LocalDateTime convertToPresentation(String value, ValueContext context) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    return DateConverter.getDateTimeWithType(value);
  }

}
