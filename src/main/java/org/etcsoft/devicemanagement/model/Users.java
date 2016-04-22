package org.etcsoft.devicemanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

import java.util.List;
import java.util.Map;

/**
 * Created by mauro on 04/04/16.
 */
@Value //immutable
@Builder // builder
@ToString //override toString method
public final class Users {

    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DEVICES = "devices";
    public static final String PROPERTIES = "properties";

    @JsonProperty(value = USER)
    String user;
    @JsonProperty(value = PASSWORD)
    String passwd;
    @JsonProperty(value = DEVICES)
    @Singular
    List<Device> devices;
    @JsonProperty(value = PROPERTIES)
    @Singular
    Map<String,Object> properties;
}
