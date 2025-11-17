package use_case.authentication;

import entity.User;

import java.util.Optional;

public interface UserDataAccessInterface {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    void save(User user);
}


