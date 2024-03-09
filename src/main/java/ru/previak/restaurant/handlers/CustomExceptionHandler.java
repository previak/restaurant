package ru.previak.restaurant.handlers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.previak.restaurant.exceptions.BadRequestException;
import ru.previak.restaurant.exceptions.NotFoundException;

@Log4j2
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadRequestException.class, NotFoundException.class})
    public ResponseEntity<Object> exception(Exception ex, WebRequest request) throws Exception {

        log.error("Exception during execution of application", ex);

        return handleException(ex, request);
    }
}
