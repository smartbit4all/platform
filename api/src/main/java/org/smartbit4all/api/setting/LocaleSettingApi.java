package org.smartbit4all.api.setting;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.setting.bean.LocaleSettingsRoot;
import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the main holder for the locale settings. All the keys are available at the fully
 * qualified name of the variables. But on the other hand we will be able to add translations that
 * defines the translations between the default language and the target languages.
 * 
 * @author Peter Boros
 */
public final class LocaleSettingApi implements InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(LocaleSettingApi.class);

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

  @Autowired(required = false)
  private List<LocaleOption> localeOptions;

  @Autowired
  private StorageApi storageApi;

  /**
   * The default locale for the locale specific Strings.
   */
  private Locale defaultLocale;

  /**
   * Adds a key to the registry of the Api. This function is dedicated for inner usage, don't call
   * this!
   * 
   * @param key
   * @param defaultValue
   */
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

  /**
   * Clears all translations including the key and literal translations also.
   */
  public void clearTranslations() {
    localeSpecificKeyBasedTranslations.clear();
    localeSpecificTranslations.clear();
  }

  public final String get(String key) {
    return get(defaultLocale, key);
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

  /**
   * This function analyze the given class to discover the {@link LocaleString} fields. We add this
   * API for them to enable locale specific behavior for them.
   * 
   * @param clazz
   */
  public void analyzeLocaleStrings(LocaleOption option) {
    // Let's check the static LocaleString
    Field[] fields = option.getClass().getFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      if (field.getType().isAssignableFrom(LocaleString.class)) {
        try {
          LocaleString localeString = (LocaleString) field.get(option);
          if (localeString != null) {
            localeString.setApi(this);
            String key = ReflectionUtility.getQualifiedName(field);
            localeString.setKey(key);
            add(key,
                localeString.getDefaultValue());
          }
        } catch (IllegalArgumentException | IllegalAccessException e) {
          log.debug("Unable to access the value of the " + field, e);
        }
      }
    }
  }

  /**
   * In the after property set function we discover the {@link #localeOptions} list to set up the
   * enclosed {@link LocaleString}s.
   * 
   * @throws Exception
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    if (localeOptions != null) {
      for (LocaleOption localeOption : localeOptions) {
        analyzeLocaleStrings(localeOption);
        Storage<LocaleSettingsRoot> storageLocale = storageApi.get(LocaleSettingsRoot.class);
        if (storageLocale != null) {
          LocaleResource resourceAnnotation =
              localeOption.getClass().getAnnotation(LocaleResource.class);
          // Load the built-in setting from the class path.
          for (int i = 0; i < resourceAnnotation.value().length; i++) {
            // storageLocale.load(UriUtils.formatUriHost(source) resourceAnnotation.value()[i]);
          }
        }
      }
    }
  }

  public final Locale getDefaultLocale() {
    return defaultLocale;
  }

  public final void setDefaultLocale(Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

}
