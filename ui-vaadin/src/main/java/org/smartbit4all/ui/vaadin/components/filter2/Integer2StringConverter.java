package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.DateConverter;
import com.google.common.base.Strings;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class Integer2StringConverter implements Converter<Integer, String> {

  private static final long serialVersionUID = -3345733654566609687L;

  @Override
  public Result<String> convertToModel(Integer option, ValueContext context) {
    if (option == null) {
      return Result.ok(null);
    }
    return Result.ok(DateConverter.PREFIX_STRING + option.toString());
  }

  @Override
  public Integer convertToPresentation(String value, ValueContext context) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    if (!value.startsWith(DateConverter.PREFIX_STRING)) {
      return null;
    }
    return Integer.valueOf(value.substring(DateConverter.PREFIX_STRING.length()));
  }
  
}
