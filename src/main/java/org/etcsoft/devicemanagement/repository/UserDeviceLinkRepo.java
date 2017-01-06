package org.etcsoft.devicemanagement.repository;

import java.util.UUID;

public interface UserDeviceLinkRepo {
    void linkDevicesToUser(UUID deviceId, String username);
    void unLinkDevicesToUser(UUID deviceId, String username);
}
