package de.twomartens.template.controller;

import de.twomartens.template.exception.HttpStatusException;
import de.twomartens.template.model.dto.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(HttpStatusException.class)
  public ResponseEntity<ErrorMessage> handleException(HttpStatusException e) {
    if (e.getCause() != null) {
      log.info(e.getCause().toString(), e.getCause());
    } else {
      log.info(e.toString());
    }
    return ResponseEntity.status(e.getStatus()).body(ErrorMessage.builder()
        .message(e.getMessage())
        .build());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException e) {
    log.error("unexpected exception occurred", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.builder()
        .message(e.getMessage())
        .build());
  }

}
