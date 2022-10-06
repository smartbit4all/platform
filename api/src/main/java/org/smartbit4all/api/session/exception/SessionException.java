package org.smartbit4all.api.session.exception;

public abstract class SessionException extends RuntimeException {

  private static final long serialVersionUID = -4480489962013496422L;

  private final String errorCode;

  protected SessionException(String errorCode, String msg) {
    super(msg);
    this.errorCode = errorCode;
  }

  protected SessionException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

}
