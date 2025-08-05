package org.smartbit4all.api.mimetype;

import org.smartbit4all.api.setting.LocaleSettingApi;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ConverterImpl implements Converter {

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  protected String from;
  protected String to;

  protected ConverterImpl() {
    super();
  }

  @Override
  public String getFrom() {
    return from;
  }

  @Override
  public String getTo() {
    return to;
  }

}
