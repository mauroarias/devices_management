package org.etcsoft.devicemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mauro on 04/04/16.
 */
@Value //immutable
@Builder // builder
@ToString //override toString method
public final class User {

    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DEVICES = "deviceNames";
    public static final String PROPERTIES = "properties";

    @JsonProperty(value = USER)
    private final String user;

    @JsonProperty(value = PASSWORD)
    private final String passwd;

    @JsonProperty(value = DEVICES)
    @Singular
    private final List<String> deviceNames;

    @JsonProperty(value = PROPERTIES)
    @Singular
    private final Map<String,Object> properties;

    @JsonIgnore
    public List<String> getDeviceNames() {
        return new ArrayList<>(deviceNames);
    }

    @JsonIgnore
    public Map<String,Object> getProperties() {
        return new HashMap<>(properties);
    }
}

