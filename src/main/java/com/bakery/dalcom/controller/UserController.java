package com.bakery.dalcom.controller;

import com.bakery.dalcom.domain.User;
import com.bakery.dalcom.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/{userEid}")
    public User getUserByUsername(@PathVariable int userEid) {
        return userService.findByUserEid(userEid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
