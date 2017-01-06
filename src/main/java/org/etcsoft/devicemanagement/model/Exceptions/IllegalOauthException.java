package org.etcsoft.devicemanagement.model.Exceptions;

import lombok.Getter;
import org.etcsoft.devicemanagement.model.Enums.AouthErrors;
import org.etcsoft.devicemanagement.model.Enums.ErrorCodes;

public final class IllegalOauthException extends IllegalException {

    @Getter
    private final AouthErrors aouthError;

    /**
     * It throws the exception with message.
     * @param aouthError Authentication error, supporting 401 and 403
     * @param errorCode AppDirect errorcode
     * @param message Message added to the exception
     */
    public IllegalOauthException(AouthErrors aouthError, ErrorCodes errorCode, String message) {
        super(errorCode, message);
        this.aouthError = aouthError;
    }
}
