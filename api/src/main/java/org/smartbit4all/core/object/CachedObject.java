package org.smartbit4all.core.object;

/**
 * The cache object is a container for the cached objects in the memory. It contains the object and
 * the exact time of the last modification in long.
 * 
 * @author Peter Boros
 *
 */
public class CachedObject {

  long lastModification;

  Object object;

  public CachedObject(long lastModification, Object object) {
    super();
    this.lastModification = lastModification;
    this.object = object;
  }

}
