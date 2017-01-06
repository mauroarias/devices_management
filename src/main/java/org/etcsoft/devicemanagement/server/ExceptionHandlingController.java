package org.etcsoft.devicemanagement.server;

import org.etcsoft.devicemanagement.model.Exceptions.IllegalException;
import org.etcsoft.devicemanagement.model.Exceptions.IllegalOauthException;
import org.etcsoft.devicemanagement.model.FailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * It handles exceptions from the controller.
 */
@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(IllegalException.class)
    public ResponseEntity handlerIllegalEventException(IllegalException ex) {
        return new ResponseEntity(
                FailResponse.buildFailResponse(ex.getErrorCode(), ex.getMessage()),
                ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(IllegalOauthException.class)
    public ResponseEntity<FailResponse> handlerIllegalOauthEventException(IllegalOauthException ex) {
        return new ResponseEntity<>(
                FailResponse.buildFailResponse(ex.getErrorCode(), ex.getMessage()),
                ex.getAouthError().getHttpCode());
    }

}
