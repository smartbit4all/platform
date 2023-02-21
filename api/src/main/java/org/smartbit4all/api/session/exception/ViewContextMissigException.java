package org.smartbit4all.api.session.exception;

public class ViewContextMissigException extends SessionException {

  private static final String ERROR_CODE = "session.viewcontext.missing";
  private static final String ERROR_MSG = "The ViewContext is missing!";

  public ViewContextMissigException() {
    super(ERROR_CODE, ERROR_MSG);
  }

  public ViewContextMissigException(Throwable cause) {
    super(ERROR_CODE, ERROR_MSG, cause);
  }

}
