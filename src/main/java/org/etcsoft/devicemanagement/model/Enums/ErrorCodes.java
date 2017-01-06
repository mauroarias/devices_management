package org.etcsoft.devicemanagement.model.Enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public enum ErrorCodes {
    DATABASE_ACCESS(INTERNAL_SERVER_ERROR),
    WRONG_VALIDATION(BAD_REQUEST),
    DEVICE_ALREADY_EXISTS(BAD_REQUEST),
    USER_ALREADY_EXISTS(BAD_REQUEST),
    USER_NOT_EXISTS(BAD_REQUEST),
    DEVICE_NOT_EXISTS(BAD_REQUEST),
    UNKNOWN_ERROR(INTERNAL_SERVER_ERROR);

    @Getter
    private final HttpStatus httpStatus;

    ErrorCodes(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
