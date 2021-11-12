package org.smartbit4all.core.object;

import java.util.function.Function;

/**
 * The domain objects are subject of listing and displaying. In this situation it's important to
 * have a common logic to produce the readable summary of the object. The implementations are
 * collected by the {@link ObjectApi} and we can get the summary supplier through the
 * {@link ObjectDefinition}.
 * 
 * @author Peter Boros
 */
public abstract class ObjectSummarySupplier<T> implements Function<T, String> {

  /**
   * The class of the bean that is managed by the supplier.
   */
  private Class<T> clazz;

  /**
   * Optionally we can assign a name to the given supplier if we have more than one.
   */
  private String name;

  /**
   * If we
   * 
   * @param clazz
   * @param name
   */
  public ObjectSummarySupplier(Class<T> clazz, String name) {
    super();
    this.clazz = clazz;
    this.name = name;
  }

  public ObjectSummarySupplier(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

}
