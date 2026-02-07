package com.instagram.user_service.repository;
import com.instagram.user_service.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    public User save(User user) {
        // Ako id nije postavljen, možeš automatski dodeliti
        if (user.getId() == null) {
            user.setId((long) (users.size() + 1));
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        return users.get(id);
    }
}

