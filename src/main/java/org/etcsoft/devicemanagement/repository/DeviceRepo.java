package org.etcsoft.devicemanagement.repository;

import org.etcsoft.devicemanagement.model.Device;

import java.util.List;
import java.util.Optional;

/**
 * Created by mauro on 22/04/16.
 */
public interface DeviceRepo {

    void insert(Device device);
    void update(Device device, String deviceName);
    void delete(String deviceName);
    Optional<Device> select(String deviceName);
    List<Device> selectAll();
}
