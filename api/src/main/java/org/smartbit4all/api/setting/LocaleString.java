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

  public LocaleString(String defaultValue) {
    super();
    this.defaultValue = defaultValue;
  }

  public final String get() {
    return defaultValue;
  }

}
