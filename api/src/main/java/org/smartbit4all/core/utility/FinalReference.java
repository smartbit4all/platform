package org.smartbit4all.core.utility;

/**
 * A simple object to use for collection information in the heart of a lambda or stream operation.
 * It can be useful to return some result about the stream based processing.
 * 
 * @author Peter Boros
 */
public class FinalReference<T> {

  private T value;

  public FinalReference(T value) {
    super();
    this.value = value;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }

}
