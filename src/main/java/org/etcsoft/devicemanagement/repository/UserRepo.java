package org.etcsoft.devicemanagement.repository;

import org.etcsoft.devicemanagement.model.Device;
import org.etcsoft.devicemanagement.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by mauro on 04/05/16.
 */
public interface UserRepo {

    void insert(User device);
    void update(User device, String user);
    void delete(String user);
    Optional<User> select(String user);
    List<User> selectAll();
}
