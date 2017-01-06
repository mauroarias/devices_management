package org.etcsoft.devicemanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User bean
 */
@Value //immutable
@Builder // builder
@ToString //override toString method
public final class User {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String username;
    private final String passwd;
    @Singular
    private final List<UUID> devices;

    public User(@JsonProperty(value = "first_name") String firstName,
                @JsonProperty(value = "last_name") String lastName,
                @JsonProperty(value = "email") @NotBlank(message = "User.email cannot be blank.") String email,
                @JsonProperty(value = "username") @NotBlank(message = "User.username cannot be blank.") String username,
                @JsonProperty(value = "password") String passwd,
                @JsonProperty(value = "devices") List<UUID> devices) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.passwd = passwd;
        this.devices = devices == null ? new ArrayList<>() : new ArrayList<>(devices);
    }

    public List<UUID> getDevices() {
        return new ArrayList<>(devices);
    }
}
