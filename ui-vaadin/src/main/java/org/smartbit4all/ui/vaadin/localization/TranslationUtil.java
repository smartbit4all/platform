package org.smartbit4all.ui.vaadin.localization;

import java.util.List;
import java.util.Locale;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;

public class TranslationUtil {

  private static TranslationUtil instance;
  
  private TranslationUtil() {
  }
  
  public static TranslationUtil INSTANCE() {
    if(instance == null) {
      instance = new TranslationUtil(); 
    }
    return instance;
  }
  
  public String getTranslation(String key, Object... params) {
      return getTranslation(key, getLocale(), params);
  }

  public String getTranslation(String key, Locale locale, Object... params) {
      if (getI18NProvider() == null) {
          return "!{" + key + "}!";
      }
      return getI18NProvider().getTranslation(key, locale, params);
  }

  private I18NProvider getI18NProvider() {
      return VaadinService.getCurrent().getInstantiator().getI18NProvider();
  }

  protected Locale getLocale() {
      UI currentUi = UI.getCurrent();
      Locale locale = currentUi == null ? null : currentUi.getLocale();
      if (locale == null) {
          List<Locale> locales = getI18NProvider().getProvidedLocales();
          if (locales != null && !locales.isEmpty()) {
              locale = locales.get(0);
          } else {
              locale = Locale.getDefault();
          }
      }
      return locale;
  }
}
