package org.etcsoft.devicemanagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.etcsoft.devicemanagement.model.Enums.ErrorCodes;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * response bean.
 */
//ignore null values during serialize.
@Value
@JsonInclude(NON_NULL)
public final class FailResponse {
    @JsonProperty("errorCode")
    private final ErrorCodes errorCode;
    @JsonProperty("message")
    private final String message;

    private FailResponse(ErrorCodes errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static FailResponse buildFailResponse(ErrorCodes errorCode, String message) {
        return new FailResponse(errorCode, message);
    }
}
