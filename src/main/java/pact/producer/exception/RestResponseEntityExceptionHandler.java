package pact.producer.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

  @Autowired
  private ObjectMapper objectMapper;

  @ExceptionHandler(UserNotFoundException.class)
  protected ResponseEntity<Object> userNotFound(UserNotFoundException ex, WebRequest request) {
    return handleExceptionInternal(ex, convertExceptionToJson(ex), new HttpHeaders(), NOT_FOUND, request);
  }

  @ExceptionHandler(DuplicatedScoreException.class)
  protected ResponseEntity<Object> duplicatedUser(DuplicatedScoreException ex, WebRequest request) {
    return handleExceptionInternal(ex, convertExceptionToJson(ex), new HttpHeaders(), BAD_REQUEST, request);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> genericException(Exception ex, WebRequest request) {
    return handleExceptionInternal(ex, convertExceptionToJson(ex), new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return handleExceptionInternal(ex, convertExceptionToJson(ex), headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return handleExceptionInternal(ex, convertExceptionToJson(ex), headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    LOGGER.error("Exception: ", ex);
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  private String convertExceptionToJson(Exception ex) {
    ErrorWrapper errorWrapper = new ErrorWrapper(ex.getMessage());
    try {
      return objectMapper.writeValueAsString(errorWrapper);
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not return a formatted exception: ", e);
      return "{}";
    }
  }

}
