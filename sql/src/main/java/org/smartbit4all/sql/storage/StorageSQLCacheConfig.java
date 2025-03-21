package org.smartbit4all.sql.storage;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

/**
 * Configuration for the SQL storage cache system. Allows for global cache settings and per-class
 * overrides.
 */
@Configuration
@ConfigurationProperties(prefix = "storage-sql.cache")
public class StorageSQLCacheConfig {

  /**
   * Global cache enabled flag.
   */
  private boolean enabled = true;

  /**
   * Default configuration used when no class-specific config exists.
   */
  private CacheSettings defaultSettings = new CacheSettings();

  /**
   * Per-class configurations. The key is the fully qualified class name.
   */
  private Map<String, CacheSettings> classSettings = new HashMap<>();

  private String _classname(String classname) {
    if (ObjectUtils.isEmpty(classname)) {
      return classname;
    }
    return classname.replace('_', '.');
  }

  /**
   * Gets the effective cache settings for a given class name.
   * 
   * @param className The fully qualified class name
   * @return The cache settings to use for this class
   */
  public CacheSettings getSettingsForClass(String className) {
    className = _classname(className);
    if (className == null || !classSettings.containsKey(className)) {
      return defaultSettings;
    }

    CacheSettings specificSettings = classSettings.get(className);
    // If the class has specific settings but no policy is set, inherit from default
    if (specificSettings.getPolicy() == null) {
      specificSettings.setPolicy(defaultSettings.getPolicy());
    }

    return specificSettings;
  }

  /**
   * Determines if a specific class should be cached.
   * 
   * @param className The fully qualified class name
   * @return true if the class should be cached, false otherwise
   */
  public boolean shouldCache(String className) {
    if (!enabled) {
      return false;
    }

    CacheSettings settings = getSettingsForClass(className);
    return settings.getPolicy() == CachePolicy.CACHE;
  }

  /**
   * Gets the maximum cache size for a specific class.
   * 
   * @param className The fully qualified class name
   * @return The maximum number of entries to cache for this class
   */
  public long getMaxSize(String className) {
    return getSettingsForClass(className).getMaxSize();
  }

  /**
   * Gets the maximum object size to cache for a specific class.
   * 
   * @param className The fully qualified class name
   * @return The maximum size in bytes of objects to cache for this class
   */
  public long getMaxObjectSize(String className) {
    return getSettingsForClass(className).getMaxObjectSize();
  }

  // Getters and setters

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public CacheSettings getDefaultSettings() {
    return defaultSettings;
  }

  public void setDefaultSettings(CacheSettings defaultSettings) {
    this.defaultSettings = defaultSettings;
  }

  public Map<String, CacheSettings> getClassSettings() {
    return classSettings;
  }

  public void setClassSettings(Map<String, CacheSettings> classSettings) {
    this.classSettings = classSettings;
  }

  /**
   * Settings for a cache configuration.
   */
  public static class CacheSettings {
    /**
     * Cache policy for this configuration.
     */
    private CachePolicy policy = CachePolicy.CACHE;

    /**
     * Maximum number of entries to keep in cache.
     */
    private long maxSize = 10000;

    /**
     * Maximum size of an object to cache in bytes. Objects larger than this will not be cached.
     */
    private long maxObjectSize = Long.MAX_VALUE;

    public CachePolicy getPolicy() {
      return policy;
    }

    public void setPolicy(CachePolicy policy) {
      this.policy = policy;
    }

    public long getMaxSize() {
      return maxSize;
    }

    public void setMaxSize(long maxSize) {
      this.maxSize = maxSize;
    }

    public long getMaxObjectSize() {
      return maxObjectSize;
    }

    public void setMaxObjectSize(long maxObjectSize) {
      this.maxObjectSize = maxObjectSize;
    }
  }

  /**
   * Cache policy enum for specifying how objects should be cached.
   */
  public enum CachePolicy {
    /**
     * Cache objects of this class.
     */
    CACHE,

    /**
     * Don't cache objects of this class.
     */
    IGNORE
  }
}
