package org.smartbit4all.api.invocation.exception;

/**
 * Marker interface for exceptions where a business logic exception occurred and has to be handled
 * appropriately.
 * 
 */
public class BusinessLogicException extends RuntimeException {

  public BusinessLogicException(String message) {
    super(message);
  }

  public BusinessLogicException(String message, Throwable cause) {
    super(message, cause);
  }

}
