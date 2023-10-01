package org.smartbit4all.api.setting;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import com.google.common.base.Strings;

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
  private final Map<String, Map<String, String>> localeSpecificTranslations =
      new ListBasedMap<>();

  /**
   * The translations for the keys. If we have a hit here then we must use this translation. In any
   * other case we can use the translation of the default language string.
   */
  private final Map<String, Map<String, String>> localeSpecificKeyBasedTranslations =
      new ListBasedMap<>();

  /**
   * The resource keys are calculated from the fully qualified name of the fields that holds
   * {@link LocaleString}s.
   */
  private final Map<String, String> sourceLiterals = new HashMap<>();

  private final Map<String, LocaleString> localeStringsByQualifiedName = new HashMap<>();

  @Autowired(required = false)
  private List<LocaleOption> localeOptions;

  @Autowired
  private StorageApi storageApi;

  @Autowired(required = false)
  private UserSessionApi userSessionApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  /**
   * The default locale for the locale specific Strings.
   */
  private Locale defaultLocale;

  @Autowired()
  @Qualifier("smartbit4all.messagesource")
  private MessageSource messageSource;

  public static final String SETTINGS_SCHEME = "setting";

  public LocaleSettingApi() {
    super();
  }

  /**
   * In the after property set function we discover the {@link #localeOptions} list to set up the
   * enclosed {@link LocaleString}s.
   *
   */
  private void initLocalOptions() {
    if (localeOptions != null) {
      for (LocaleOption localeOption : localeOptions) {
        analyzeLocaleStrings(localeOption);
        Storage storageLocale = storageApi.get(SETTINGS_SCHEME);
        if (storageLocale != null) {
          LocaleResource resourceAnnotation =
              localeOption.getClass().getAnnotation(LocaleResource.class);
          // Load the built-in setting from the class path.

          if (resourceAnnotation != null) {
            for (int i = 0; i < resourceAnnotation.value().length; i++) {
              // storageLocale.load(UriUtils.formatUriHost(source) resourceAnnotation.value()[i]);
            }
          } else {
            log.error("ResourceAnnotation for LocaleOption is NULL: " + localeOption);
          }
        }
      }
    }
  }

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
        localeSpecificKeyBasedTranslations.computeIfAbsent(getLocaleCode(locale),
            (l) -> new HashMap<>());
    keyTranslation.put(key, targetValue);
  }

  public void setTranslation(String defaultValue, Locale locale, String targetValue) {
    Map<String, String> translations =
        localeSpecificTranslations.computeIfAbsent(getLocaleCode(locale), l -> new HashMap<>());
    translations.put(defaultValue, targetValue);
  }

  /**
   * Clears all translations including the key and literal translations also.
   */
  public void clearTranslations() {
    localeSpecificKeyBasedTranslations.clear();
    localeSpecificTranslations.clear();
  }

  public final String get(LangString langString) {
    return get(getLocale(), langString);
  }

  public final String get(Locale locale, LangString langString) {
    if (langString == null) {
      return "*empty*";
    }
    String lang = locale.getLanguage();
    if (langString.getValueByLocale().containsKey(lang)) {
      return langString.getValueByLocale().get(lang);
    }
    return langString.getDefaultValue();
  }

  public final String get(String... keys) {
    return get(getLocale(), keys);
  }

  /**
   * Evaluates the locale specific title of the given enum value.
   *
   * @param enumValue The enum value.
   * @return
   */
  public final String get(Enum<?> enumValue) {
    if (enumValue == null) {
      return StringConstant.EMPTY;
    }
    return get(getLocale(), enumValue.getClass().getName(), enumValue.name());
  }

  public final String get(Locale locale, String... keys) {
    if (keys == null || keys.length == 0) {
      return null;
    }
    String key = String.join(StringConstant.DOT, keys);
    String value = getInternal(locale, key);
    if (value != null && !value.equals(key)) {
      // found a match
      return value;
    }
    if (keys.length == 1) {
      return key;
    }
    String[] subKeys = Arrays.copyOfRange(keys, 1, keys.length);
    return get(locale, subKeys);
  }

  private Locale getLocale() {
    Locale sessionLocale = null;
    if (sessionApi != null) {
      sessionLocale = sessionApi.getLocale();
    } else if (userSessionApi != null && userSessionApi.currentSession() != null) {
      sessionLocale = userSessionApi.currentSession().getCurrentLocale();
    }
    Locale defaultLocaleTmp = defaultLocale != null ? defaultLocale : Locales.HUNGARIAN;
    return sessionLocale != null ? sessionLocale : defaultLocaleTmp;
  }

  private final String getInternal(Locale locale, String key) {
    if (locale == null) {
      String sourceLiteral = sourceLiterals.get(key);
      return sourceLiteral != null ? sourceLiteral : key;
    }
    Map<String, String> keyBasedTranslations =
        localeSpecificKeyBasedTranslations.get(getLocaleCode(locale));
    if (keyBasedTranslations != null) {
      String keyBasedValue = keyBasedTranslations.get(key);
      if (keyBasedValue != null) {
        return keyBasedValue;
      }
    }
    // We get the default String and the translations based on this.
    String sourceLiteral = sourceLiterals.get(key);
    if (sourceLiteral != null) {
      Map<String, String> translations = localeSpecificTranslations.get(getLocaleCode(locale));
      if (translations != null) {
        String translation = translations.get(sourceLiteral);
        if (translation != null) {
          return translation;
        }
      }
      return sourceLiteral;
    }
    try {
      return messageSource.getMessage(key, null, locale);
    } catch (NoSuchMessageException e) {
      return key;
    }
  }

  /**
   * This function analyze the given class to discover the {@link LocaleString} fields. We add this
   * API for them to enable locale specific behavior for them.
   *
   * @param option
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
            localeStringsByQualifiedName.put(key, localeString);
          }
        } catch (IllegalArgumentException | IllegalAccessException e) {
          log.debug("Unable to access the value of the " + field, e);
        }
      }
    }
  }

  public final Locale getDefaultLocale() {
    return defaultLocale;
  }

  public final void setDefaultLocale(Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
    for (LocaleString localeString : localeStringsByQualifiedName.values()) {
      localeString.refresh();
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    initLocalOptions();
  }

  private String getLocaleCode(Locale locale) {
    String result = locale.getLanguage();
    String country = locale.getCountry();
    if (!Strings.isNullOrEmpty(country)) {
      result += "-" + country;
    }
    String variant = locale.getVariant();
    if (!Strings.isNullOrEmpty(variant)) {
      result += "-" + variant;
    }
    return result;

  }
}
