package org.smartbit4all.api.setting;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.smartbit4all.core.utility.ListBasedMap;

/**
 * This is the main holder for the locale settings. All the keys are available at the fully
 * qualified name of the variables. But on the other hand we will be able to add translations that
 * defines the translations between the default language and the target languages.
 * 
 * @author Peter Boros
 */
public final class LocaleSettingApi {

  /**
   * The translations for the default string for the locale. The defaults are loaded into a
   */
  private final Map<Locale, Map<String, String>> localeSpecificTranslations =
      new ListBasedMap<>();

  /**
   * The translations for the keys. If we have a hit here then we must use this translation. In any
   * other case we can use the translation of the default language string.
   */
  private final Map<Locale, Map<String, String>> localeSpecificKeyBasedTranslations =
      new ListBasedMap<>();

  /**
   * The resource keys are calculated from the fully qualified name of the fields that holds
   * {@link LocaleString}s.
   */
  private final Map<String, String> sourceLiterals = new HashMap<>();

  public void add(String key, String defaultValue) {
    sourceLiterals.put(key, defaultValue);
  }

  public void setKeyTranslation(String key, Locale locale, String targetValue) {
    Map<String, String> keyTranslation =
        localeSpecificKeyBasedTranslations.computeIfAbsent(locale, (l) -> new HashMap<>());
    keyTranslation.put(key, targetValue);
  }

  public void setTranslation(String defaultValue, Locale locale, String targetValue) {
    Map<String, String> translations =
        localeSpecificTranslations.computeIfAbsent(locale, l -> new HashMap<>());
    translations.put(defaultValue, targetValue);
  }

  public final String get(String key) {
    return get(null, key);
  }

  public final String get(Locale locale, String key) {
    if (locale == null) {
      String sourceLiteral = sourceLiterals.get(key);
      return sourceLiteral != null ? sourceLiteral : key;
    }
    Map<String, String> keyBasedTranslations = localeSpecificKeyBasedTranslations.get(locale);
    if (keyBasedTranslations != null) {
      String keyBasedValue = keyBasedTranslations.get(key);
      if (keyBasedValue != null) {
        return keyBasedValue;
      }
    }
    // We get the default String and the translations based on this.
    String sourceLiteral = sourceLiterals.get(key);
    if (sourceLiteral != null) {
      Map<String, String> translations = localeSpecificTranslations.get(locale);
      if (translations != null) {
        String translation = translations.get(sourceLiteral);
        if (translation != null) {
          return translation;
        }
      }
      return sourceLiteral;
    }
    return key;
  }

}
