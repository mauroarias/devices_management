package org.etcsoft.devicemanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.UUID;

/**
 * Created by mauro on 27/03/16.
 */
@Value //immutable
@Builder //builder
@ToString //override toString method
public final class Device {
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ID = "device_id";
    public static final String MANUFACTURE = "manufacture";
    public static final String PART_NUMBER = "part_number";
    public static final String FW_VERSION = "firmware_version";
    public static final String LAST_UPDATE = "last_update";
    public static final String STATUS = "status";
    public static final String PROPERTIES = "properties";

    @JsonProperty(value = DEVICE_NAME)
    String deviceName;
    @JsonProperty(value = DEVICE_ID)
    UUID deviceId;
    @JsonProperty(value = MANUFACTURE)
    String manufacture;
    @JsonProperty(value = PART_NUMBER)
    String partNumber;
    @JsonProperty(value = FW_VERSION)
    String fwVersion;
    @JsonProperty(value = LAST_UPDATE)
    DateTime lasUpdate;
    @JsonProperty(value = STATUS)
    boolean enabled;
    @JsonProperty(value = PROPERTIES)
    @Singular
    Map<String,Object> properties;
}
