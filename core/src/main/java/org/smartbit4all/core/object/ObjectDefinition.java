package org.smartbit4all.core.object;

/**
 * This definition must exists for every api objects managed by the given module. It contains the
 * class itself, the URI accessor / mutator and the serializing methods.
 * 
 * @author Peter Boros
 */
public class ObjectDefinition<T> {

  /**
   * The java class of the object.
   */
  private Class<T> clazz;

  /**
   * The URI property of the object. The URI is the unique identifier and storage locator of the
   * given object. It's mandatory, without it the given object is not manageable.
   */
  private PropertyMeta uriProperty;

  /**
   * The fully qualified name
   */
  private String alias;

}
