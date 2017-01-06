package org.etcsoft.devicemanagement.repository;

import org.etcsoft.devicemanagement.model.User;

import java.util.Optional;

public interface UserRepo {
    void insert(User user);
    void update(User user, String username);
    void drop(String username);
    Optional<User> select(String username);
    Boolean isExists(String username);
    void updatePassword(String username, String password);
}