package org.smartbit4all.ui.vaadin.localization;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.vaadin.flow.i18n.I18NProvider;

@Component
public class TranslationProvider implements I18NProvider {

  public static final String BUNDLE_PREFIX = "locale.translate";

  public final Locale LOCALE_HU = new Locale("hu");
  public final Locale LOCALE_HU_HU = new Locale("hu", "HU");
  public final Locale LOCALE_EN = new Locale("en");
  public final Locale LOCALE_EN_GB = new Locale("en", "GB");

  private List<Locale> locales =
      Collections.unmodifiableList(Arrays.asList(LOCALE_HU, LOCALE_HU_HU, LOCALE_EN, LOCALE_EN_GB));

  @Override
  public List<Locale> getProvidedLocales() {
    return locales;
  }

  @Override
  public String getTranslation(String key, Locale locale, Object... params) {
    if (key == null) {
      LoggerFactory.getLogger(TranslationProvider.class.getName())
          .warn("Got lang request for key with null value!");
      return "";
    }

    Locale derivedLocale = locale;
    if (locale.equals(LOCALE_HU)) {
      derivedLocale = LOCALE_HU_HU;
    } else if (locale.equals(LOCALE_EN)) {
      derivedLocale = LOCALE_EN_GB;
    }

    final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, derivedLocale);

    String value;
    try {
      value = bundle.getString(key);
    } catch (final MissingResourceException e) {
      return handleMissingKey(key, derivedLocale, e);
    }
    if (params.length > 0) {
      value = MessageFormat.format(value, params);
    }
    return value;
  }

  protected String handleMissingKey(String key, Locale derivedLocale, final MissingResourceException e) {
    LoggerFactory.getLogger(TranslationProvider.class.getName()).warn("Missing resource", e);
    String translation = "!" + derivedLocale.getLanguage() + ": " + key;
    return translation;
  }
}

