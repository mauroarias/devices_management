package org.etcsoft.devicemanagement.model;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.UUID;

import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertEquals;

public class DeviceTest {
    @Rule
    public final ExpectedException thrownExpected = ExpectedException.none();

    @Test
    public void BuildDevice()
    {
        UUID uuid = UUID.randomUUID();
        String fwVersion = "1.3.2";
        String manufacture = "Odroid";
        DateTime datetime = DateTime.now().withZone(UTC);
        String partnumber = "usijc21.3";
        Device device = Device.builder()
                .deviceId(uuid)
                .fwVersion(fwVersion)
                .manufacture(manufacture)
                .lasUpdate(datetime)
                .partNumber(partnumber)
                .property("design", "x-model")
                .build();

        assertEquals(uuid, device.getDeviceId());
        assertEquals(fwVersion, device.getFwVersion());
        assertEquals(manufacture, device.getManufacture());
        assertEquals(datetime, device.getLasUpdate());
        assertEquals(partnumber, device.getPartNumber());
        assertEquals(new HashMap<String, String>()
            {{
                put("design", "x-model");
            }},
                device.getProperties());
    }
}
