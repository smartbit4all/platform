package org.smartbit4all.ui.vaadin.localization;

import java.util.Locale;
import java.util.MissingResourceException;

public class PossibleTranslationProvider extends TranslationProvider {

  @Override
  protected String handleMissingKey(String key, Locale derivedLocale, MissingResourceException e) {
    // simply return the key when no translation has found
    return key;
  }
  
}
