package org.smartbit4all.api.setting;

import org.springframework.stereotype.Service;


@Service
public class MyModuleLocale implements LocaleOption {

  public static LocaleString apple = new LocaleString("apple");

  public static LocaleString pear = new LocaleString("pear");

  public LocaleString peach = new LocaleString("peach");

}
