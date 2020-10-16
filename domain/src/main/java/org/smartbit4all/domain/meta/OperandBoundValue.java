package org.smartbit4all.domain.meta;

/**
 * This class is injected for the {@link OperandProperty} to access its bound value.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public abstract class OperandBoundValue<T> {

  public abstract T getValue();

}
