package org.etcsoft.devicemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mauro on 27/03/16.
 */
@Value //immutable
@Builder //builder
@ToString //override toString method
public final class Device {
    public static final String DEVICE_NAME = "device_name";
    public static final String MANUFACTURE = "manufacture";
    public static final String PART_NUMBER = "part_number";
    public static final String FW_VERSION = "firmware_version";
    public static final String LAST_UPDATE = "last_update";
    public static final String STATUS = "status";
    public static final String PROPERTIES = "properties";

    @JsonProperty(value = DEVICE_NAME)
    private final String deviceName;

    @JsonProperty(value = MANUFACTURE)
    private final String manufacture;

    @JsonProperty(value = PART_NUMBER)
    private final String partNumber;

    @JsonProperty(value = FW_VERSION)
    private final String fwVersion;

    @JsonProperty(value = LAST_UPDATE)
    private final DateTime lasUpdate;

    @JsonProperty(value = STATUS)
    private final Boolean enabled;

    @JsonProperty(value = PROPERTIES)
    @Singular
    private final Map<String,Object> properties;

    @JsonIgnore
    public Map<String,Object> getProperties() {
        return new HashMap<>(properties);
    }
}
