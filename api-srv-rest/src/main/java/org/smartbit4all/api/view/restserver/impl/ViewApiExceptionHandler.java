package org.smartbit4all.api.view.restserver.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.view.bean.ViewApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// @ControllerAdvice(assignableTypes = {ViewApiMarker.class})
public class ViewApiExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ViewApiExceptionHandler.class);

  @ExceptionHandler(RuntimeException.class)
  protected ResponseEntity<Object> handleViewApiException(RuntimeException ex) {
    log.warn("Handling runtime error", ex);
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ViewApiError()
            .message(ex.getMessage()));
  }

}
