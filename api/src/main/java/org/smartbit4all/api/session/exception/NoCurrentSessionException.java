package org.smartbit4all.api.session.exception;

import org.smartbit4all.api.session.SessionApi;

/**
 * Dedicated exception to handle missing current sessions using the {@link SessionApi}
 */
public class NoCurrentSessionException extends SessionException {

  private static final long serialVersionUID = -608220389455960075L;

  public NoCurrentSessionException(String errorCode, String msg) {
    super(errorCode, msg);
  }

  public NoCurrentSessionException(String errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }

  public NoCurrentSessionException(String msg) {
    super(null, msg);
  }

  public NoCurrentSessionException(String message, Throwable cause) {
    super(null, message, cause);
  }

}
