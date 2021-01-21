package org.smartbit4all.api.object;

/**
 * The operations of the {@link ObjectChange} events. The operations express the changes inside the
 * stateful api. If we start editing an existing Customer object then we will be notified about it
 * as new. Because it's a newly referred object in our object hierarchy.
 * 
 * @author Peter Boros
 */
public enum ChangeOperation {

  /**
   * The new means that the object is referred as new in the stateful api. The object itself can be
   * a new one or an existing one in the repository.
   */
  NEW,
  /**
   * The modified means that we have embedded modifications also.
   */
  MODIFIED,
  /**
   * The reference is deleted from the object hierarchy so we can remove the whole subtree.
   */
  DELETED

}
