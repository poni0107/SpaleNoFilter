package com.instagram.user_service.service;

import com.instagram.user_service.model.User;
import com.instagram.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User getUser(Long id) {
        User user = repo.findById(id); // HashMap vraća User direktno
        if (user == null) {
            throw new RuntimeException("User not found with id " + id);
        }
        return user;
    }

    public User createUser(User user) {
        return repo.save(user);
    }
}
