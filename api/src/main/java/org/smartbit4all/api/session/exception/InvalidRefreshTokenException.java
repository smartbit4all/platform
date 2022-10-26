package org.smartbit4all.api.session.exception;

public class InvalidRefreshTokenException extends SessionException {

  private static final long serialVersionUID = 3921042424847051111L;

  private static final String ERROR_CODE = "session.refreshtoken.invalid";
  private static final String ERROR_MSG = "The given refresh token is invalid!";

  public InvalidRefreshTokenException() {
    super(ERROR_CODE, ERROR_MSG);
  }

  public InvalidRefreshTokenException(Throwable cause) {
    super(ERROR_CODE, ERROR_MSG, cause);
  }

}
