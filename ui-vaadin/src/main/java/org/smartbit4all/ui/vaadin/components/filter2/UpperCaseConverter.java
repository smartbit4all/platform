package org.smartbit4all.ui.vaadin.components.filter2;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class UpperCaseConverter implements Converter<String, String> {

  @Override
  public Result<String> convertToModel(String value, ValueContext context) {
    String result = value == null ? null : value.toUpperCase();
    return Result.ok(result);
  }

  @Override
  public String convertToPresentation(String value, ValueContext context) {
    String result = value == null ? null : value.toUpperCase();
    return result;
  }

}
