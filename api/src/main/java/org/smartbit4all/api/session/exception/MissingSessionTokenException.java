package org.smartbit4all.api.session.exception;

public class MissingSessionTokenException extends SessionException {

  private static final String ERROR_CODE = "session.badrequest.missingtoken";
  private static final String ERROR_MSG = "There is no session id passed in the request!";

  public MissingSessionTokenException() {
    super(ERROR_CODE, ERROR_MSG);
  }

  public MissingSessionTokenException(Throwable cause) {
    super(ERROR_CODE, ERROR_MSG, cause);
  }

}
