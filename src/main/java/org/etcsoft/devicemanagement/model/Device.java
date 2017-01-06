package org.etcsoft.devicemanagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import java.util.*;

/**
 * Device bean
 */
@Value //immutable
@Builder //builder
@ToString //override toString method
public final class Device {
    private final UUID deviceId;
    private final String manufacture;
    private final String partNumber;
    private final String fwVersion;
    private final DateTime lasUpdate;
    @Singular
    private final Map<String,Object> properties;
    @Singular
    private final List<String> owners;

    @JsonCreator
    public Device(@JsonProperty(value = "device_id") @NotBlank(message = "device.uuid cannot be blank.") UUID deviceId,
                  @JsonProperty(value = "manufacture") String manufacture,
                  @JsonProperty(value = "part_number") @NotBlank(message = "device.part_number cannot be blank.") String partNumber,
                  @JsonProperty(value = "firmware_version") @NotBlank(message = "device.firmware_version cannot be blank.") String fwVersion,
                  @JsonProperty(value = "last_update") DateTime lasUpdate,
                  @JsonProperty(value = "properties") Map<String, Object> properties,
                  @JsonProperty(value = "owners") List<String> owners) {
        this.deviceId = deviceId;
        this.manufacture = manufacture;
        this.partNumber = partNumber;
        this.fwVersion = fwVersion;
        this.lasUpdate = lasUpdate;
        this.properties = properties == null ? new HashMap<>() : new HashMap<>(properties);
        this.owners = owners == null ? new ArrayList<>() : new ArrayList<>(owners);
    }

    public Map<String,Object> getProperties() {
        return new HashMap<>(properties);
    }

    public List<String> getOwners() {
        return new ArrayList<>(owners);
    }
}
