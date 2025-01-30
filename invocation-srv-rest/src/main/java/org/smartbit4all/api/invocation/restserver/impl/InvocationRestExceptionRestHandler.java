package org.smartbit4all.api.invocation.restserver.impl;

import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InvocationRestExceptionRestHandler extends ResponseEntityExceptionHandler {


  @ExceptionHandler(InvocationRestException.class)
  protected ResponseEntity<Object> handleSessionException(InvocationRestException ex) {
    if (ex.ex instanceof ApiNotFoundException) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new InvocationError().definition(
            ex.ex.getClass().getName()).message(ex.ex.getMessage())
            .stackTrace(Invocations.listOfStackTrace(ex.ex)));
  }

}
