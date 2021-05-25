package org.smartbit4all.api.setting;

/**
 * This variable is defined to have a default value and if it's set then use the UserSettingApi to
 * access the locale dependent version of the value. It works without any UserSettingApi but in this
 * case it will return the default value.
 * 
 * @author Peter Boros
 *
 */
public final class LocaleString {

  /**
   * The default value for the given resource.
   */
  private final String defaultValue;

  private String key;

  /**
   * The user settings for the application. It must be bound to the user typically it's a spring
   * session scoped bean that is related to the user session.
   */
  private LocaleSettingApi api;

  /**
   * Constructs a locale string with a default value.
   * 
   * @param defaultValue
   */
  public LocaleString(String defaultValue) {
    super();
    this.defaultValue = defaultValue;
  }

  /**
   * Get the relevant locale specific value for the local string.
   * 
   * @return
   */
  public final String get() {
    return api == null || key == null ? defaultValue : api.get(key);
  }

  /**
   * @return The default value for the string
   */
  final String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Set the api instance for the string.
   * 
   * @param api
   */
  final void setApi(LocaleSettingApi api) {
    this.api = api;
  }

  /**
   * Set the key for the given LocaleString.
   * 
   * @param key
   */
  final void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return get();
  }

}
