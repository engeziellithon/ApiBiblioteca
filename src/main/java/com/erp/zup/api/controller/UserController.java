package com.erp.zup.api.controller;

import com.erp.zup.domain.User;
import com.erp.zup.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{id}")
    public User user(@PathVariable("id") Long id) {
        Optional<User> userFind = Optional.ofNullable(userService.GetUserById(id));

        return userFind.orElse(null);
    }

    @PostMapping
    public User user(@RequestBody User user) {
        return userService.CreateUser(user);
    }

    @GetMapping("/list")
    public List<User> list() {
        return userService.ListUsers();
    }

    @GetMapping("/findByName/{name}")
    public List<User> findByName(@PathVariable("name") String name) {
        return userService.GetUsersByName(name);
    }
}
