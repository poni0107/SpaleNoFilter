package com.instagram.user_service.controller;

import com.instagram.user_service.model.User;
import com.instagram.user_service.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }
}
