package org.smartbit4all.sec.localauth;

import org.springframework.security.core.AuthenticationException;

public class BruteForceLockException extends AuthenticationException {

  public BruteForceLockException(String message) {
    super(message);
  }

  public BruteForceLockException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
