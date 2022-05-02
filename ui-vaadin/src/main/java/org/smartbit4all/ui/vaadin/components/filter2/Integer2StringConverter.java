package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.util.FilterConfigs;
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
    return Result.ok(FilterConfigs.PREFIX_INTEGER + option.toString());
  }

  @Override
  public Integer convertToPresentation(String value, ValueContext context) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    if (!value.startsWith(FilterConfigs.PREFIX_INTEGER)) {
      return null;
    }
    return Integer.valueOf(value.substring(FilterConfigs.PREFIX_INTEGER.length()));
  }

}
