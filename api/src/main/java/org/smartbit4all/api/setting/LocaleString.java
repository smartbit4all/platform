package org.smartbit4all.api.setting;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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
   * The consumers of the given string value.
   */
  List<WeakReference<Consumer<String>>> consumers = new LinkedList<>();

  /**
   * Constructs a locale string with a default value.
   * 
   * @param defaultValue
   */
  public LocaleString(String defaultValue) {
    super();
    this.defaultValue = defaultValue;
  }

  private final void purge() {
    consumers.removeIf(r -> r.get() == null);
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


  /**
   * Get the key for the given LocaleString.
   * 
   */
  public String getKey() {
    return key;
  }

  /**
   * This function register a consumer of this locale specific string. It can be a setTitle or
   * setLabel function. The apply itself will set the value but later on if there is any change in
   * the locale settings then this consumer will be refreshed.
   * 
   * @param consumer
   */
  public void apply(Consumer<String> consumer) {
    purge();
    consumers.add(new WeakReference<Consumer<String>>(consumer));
    consumer.accept(get());
  }

  /**
   * Refreshes all the consumer and purge the dead references.
   */
  public void refresh() {
    for (Iterator<WeakReference<Consumer<String>>> iterator = consumers.iterator(); iterator
        .hasNext();) {
      Consumer<String> consumer = iterator.next().get();
      if (consumer != null) {
        consumer.accept(get());
      } else {
        iterator.remove();
      }
    }
  }

}
