/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.localization;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
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

    ResourceBundle bundle;

    try {
      bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, derivedLocale);
    } catch (Throwable tr) {
      bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, LOCALE_HU_HU);
    }

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

  protected String handleMissingKey(String key, Locale derivedLocale,
      final MissingResourceException e) {
    Logger logger = LoggerFactory.getLogger(TranslationProvider.class.getName());
    if (logger.isDebugEnabled()) {
      logger.debug("Missing resource", e);
    } else {
      logger.warn("Missing resource: " + key);
    }
    String translation = "!" + derivedLocale.getLanguage() + ": " + key;
    return translation;
  }
}

