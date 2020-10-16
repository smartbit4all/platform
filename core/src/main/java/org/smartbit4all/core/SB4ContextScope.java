package org.smartbit4all.core;

/**
 * If the {@link SB4Service} implements this then it has its own context. So invoking an operation
 * in this service means that we change the active context in the invocation handler. So it will be
 * the active context until this operation will end.
 * 
 * @author Peter Boros
 *
 */
public interface SB4ContextScope {

  /**
   * The accessor method for the context owned by the scope.
   * 
   * @return
   */
  SB4Context context();

}
