package org.etcsoft.devicemanagement.model.Exceptions;

import lombok.Getter;
import org.etcsoft.devicemanagement.model.Enums.ErrorCodes;

/**
 * This exception is thrown when an error happened.
 */
public class IllegalException extends RuntimeException {
    @Getter
    private final ErrorCodes errorCode;

    /**
     * It throws the exception with a message
     * @param errorCode AppDirect error code
     * @param message Message added to the exception
     */
    public IllegalException(ErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
