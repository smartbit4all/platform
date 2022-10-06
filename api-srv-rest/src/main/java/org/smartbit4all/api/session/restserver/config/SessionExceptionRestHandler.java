package org.smartbit4all.api.session.restserver.config;

import org.smartbit4all.api.session.bean.ApiError;
import org.smartbit4all.api.session.exception.SessionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SessionExceptionRestHandler extends ResponseEntityExceptionHandler {


  @ExceptionHandler(SessionException.class)
  protected ResponseEntity<Object> handleSessionException(SessionException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError().message(ex.getMessage()).code(ex.getErrorCode()));
  }

}
