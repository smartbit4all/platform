package org.smartbit4all.api.constraint;

/**
 * The change object of the constraint change. We will have a list of changes after rendering the
 * result of the current transaction.
 * 
 * @author Peter Boros
 *
 * @param <C>
 */
public class ConstraintChange<C> {

  private final String path;

  private final C newValue;

  public ConstraintChange(String path, C newValue) {
    super();
    this.path = path;
    this.newValue = newValue;
  }

  public final C getNewValue() {
    return newValue;
  }

}
