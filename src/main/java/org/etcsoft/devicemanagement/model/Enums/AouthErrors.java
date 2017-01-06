package org.etcsoft.devicemanagement.model.Enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum AouthErrors {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    FORBIDDEN(HttpStatus.FORBIDDEN);

    @Getter
    private final HttpStatus httpCode;

    AouthErrors(HttpStatus httpCode) {
        this.httpCode = httpCode;
    }
}
