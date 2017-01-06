package org.etcsoft.devicemanagement.services;

import org.etcsoft.devicemanagement.model.User;

public interface UserService {
    User create(User user);
    User get(String username);
    void update(User user, String username);
    void updatePassword(String username, String password);
    void delete(String username);
}
