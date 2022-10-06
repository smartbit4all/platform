package org.smartbit4all.api.session.exception;

public class ExpiredSessionException extends SessionException {

  private static final long serialVersionUID = -1818109755207740233L;

  private static final String ERROR_CODE = "session.expired";
  private static final String ERROR_MSG = "The session has expired!";

  public ExpiredSessionException() {
    super(ERROR_CODE, ERROR_MSG);
  }

  public ExpiredSessionException(Throwable cause) {
    super(ERROR_CODE, ERROR_MSG, cause);
  }

}
