package org.smartbit4all.core.object;

import java.util.List;
import java.util.Map;

/**
 * This helper can enclose a {@link Map} that contains Objects as values identified by Strings.
 * Typically an object as map is something like this.
 * 
 * @author Peter Boros
 */
public class ObjectMapHelper {

  private static final String VALUE_NOT_FOUND_IN = " value not found in ";

  private static final String VALUE_IS_EMPTY_IN = " value is empty in ";

  private final String name;

  private final Map<String, Object> map;

  private final ObjectApi objectApi;

  public ObjectMapHelper(Map<String, Object> map, ObjectApi objectApi, String name) {
    super();
    this.map = map;
    this.objectApi = objectApi;
    this.name = name;
  }

  public final Map<String, Object> getMap() {
    return map;
  }

  public boolean contains(String paramName) {
    return map.containsKey(paramName);
  }

  /**
   * Checks if the given parameter exists in the given map and throw an
   * {@link IllegalArgumentException} if the given key is missing.
   * 
   * @param <T> The type we are looking for.
   * @param key The key of the value.
   * @param clazz The type class of the value.
   * @return The value if exists. It can be null anyway if the key exists but the value is set to
   *         null.
   */
  public <T> T require(String key, Class<T> clazz) {
    if (!map.containsKey(key)) {
      throw new IllegalArgumentException(key + VALUE_NOT_FOUND_IN + name);
    }
    return get(key, clazz);
  }

  /**
   * Checks if the given parameter exists and not null in the given map and throw an
   * {@link IllegalArgumentException} if the value is null even if the key doesn't exist or the
   * value is set to null.
   * 
   * @param <T> The type we are looking for.
   * @param key The key of the value.
   * @param clazz The type class of the value.
   * @return The value if exists and not null
   */
  public <T> T requireNonNull(String key, Class<T> clazz) {
    if (map.get(key) == null) {
      throw new IllegalArgumentException(key + VALUE_IS_EMPTY_IN + name);
    }
    return get(key, clazz);
  }

  /**
   * Checks if the given parameter exists and not null in the given map. If the default object is
   * null also throw an {@link IllegalArgumentException}.
   * 
   * @param <T> The type we are looking for.
   * @param key The key of the value.
   * @param clazz The type class of the value.
   * @param defaultObj
   * @return The value if exists and not null
   */
  public <T> T requireNonNullElse(String key, Class<T> clazz, T defaultObj) {
    T result = get(key, clazz);
    result = result == null ? defaultObj : result;
    if (result == null) {
      throw new IllegalArgumentException(key + VALUE_IS_EMPTY_IN + name);
    }
    return result;
  }

  /**
   * Checks if the given parameter exists in the given map and throw an
   * {@link IllegalArgumentException} if the given key is missing.
   * 
   * @param <T> The type we are looking for.
   * @param key The key of the value.
   * @param clazz The type class of the value.
   * @return The value if exists. It can be null anyway if the key exists but the value is set to
   *         null.
   */
  public <T> List<T> requireAsList(String key, Class<T> clazz) {
    if (!map.containsKey(key)) {
      throw new IllegalArgumentException(key + VALUE_NOT_FOUND_IN + name);
    }
    return getAsList(key, clazz);
  }

  /**
   * Checks if the given parameter exists and not null in the given map and throw an
   * {@link IllegalArgumentException} if the value is null even if the key doesn't exist or the
   * value is set to null.
   * 
   * @param <T> The type we are looking for.
   * @param key The key of the value.
   * @param clazz The type class of the value.
   * @return The value if exists and not null
   */
  public <T> List<T> requireNonNullAsList(String key, Class<T> clazz) {
    if (map.get(key) == null) {
      throw new IllegalArgumentException(key + VALUE_IS_EMPTY_IN + name);
    }
    return getAsList(key, clazz);
  }

  /**
   * Checks if the given parameter exists and not null in the given map. If the default object is
   * null also throw an {@link IllegalArgumentException}.
   * 
   * @param <T> The type we are looking for.
   * @param key The key of the value.
   * @param clazz The type class of the value.
   * @param defaultList
   * @return The value if exists and not null
   */
  public <T> List<T> requireNonNullElseAsList(String key, Class<T> clazz, List<T> defaultList) {
    List<T> result = getAsList(key, clazz);
    result = result == null ? defaultList : result;
    if (result == null) {
      throw new IllegalArgumentException(key + VALUE_IS_EMPTY_IN + name);
    }
    return result;
  }

  /**
   * Retrieve the object from the underlying map as the required type implies using object api. It
   * replace the map entry with the typed object to enhance further retrievals and to ensure that
   * the retrieved object can be modified.
   * 
   * @param <T>
   * @param key
   * @param clazz
   * @return
   */
  public <T> T get(String key, Class<T> clazz) {
    Object param = map.get(key);
    if (param == null) {
      return null;
    }
    T typedParam = objectApi.asType(clazz, param);
    map.put(key, typedParam);
    return typedParam;
  }

  /**
   * Retrieve the object from the underlying map as the required type implies using object api. It
   * replace the map entry with the typed object to enhance further retrievals and to ensure that
   * the retrieved object can be modified.
   * 
   * @param <T>
   * @param key
   * @param clazz
   * @return
   */
  public <T> List<T> getAsList(String key, Class<T> clazz) {
    Object param = map.get(key);
    if (param == null) {
      return null;
    }
    if (!(param instanceof List)) {
      throw new IllegalArgumentException(key + " value is not List<> in " + name);
    }
    List<T> result = objectApi.asList(clazz, (List<?>) param);
    map.put(key, result);
    return result;
  }

}
