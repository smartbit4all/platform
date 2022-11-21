package org.smartbit4all.core.constraint;

import org.smartbit4all.core.utility.StringConstant;

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

  public final String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return path + StringConstant.COLON_SPACE + newValue;
  }

}
