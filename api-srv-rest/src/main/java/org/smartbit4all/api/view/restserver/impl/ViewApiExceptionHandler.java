package org.smartbit4all.api.view.restserver.impl;

import java.text.MessageFormat;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.bean.ViewApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// @ControllerAdvice(assignableTypes = {ViewApiMarker.class})
public class ViewApiExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${view.exception.hide-technical-exceptions:false}")
  protected boolean hideTechnicalExceptions;

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  private static final Logger log = LoggerFactory.getLogger(ViewApiExceptionHandler.class);

  @ExceptionHandler(RuntimeException.class)
  protected ResponseEntity<Object> handleViewApiException(RuntimeException ex) {
    if (hideTechnicalExceptions) {
      if (!isBusinessLogicException(ex)) {
        UUID errorId = UUID.randomUUID();
        log.error("Technical error " + errorId, ex);
        String message = localeSettingApi.get("view.exception.technical");
        message = MessageFormat.format(
            message, errorId);
        return handleViewApiException(message);
      }
      log.trace(ex.getMessage(), ex);
    } else {
      log.warn("Handling runtime error", ex);
    }
    return handleViewApiException(ex.getMessage());
  }

  protected ResponseEntity<Object> handleViewApiException(String message) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ViewApiError()
            .message(message));
  }

  protected boolean isBusinessLogicException(RuntimeException ex) {
    return ex instanceof BusinessLogicException
        || (ex instanceof NullPointerException
            && ex.getMessage() != null
            && !ex.getMessage().endsWith(" is null"));
  }
}
