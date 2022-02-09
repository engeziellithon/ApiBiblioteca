package com.erp.zup.api.controller;

import com.erp.zup.api.VM.UserVM;
import com.erp.zup.api.config.mapper.MapperUtil;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private MapperUtil mapperUtil;

    @GetMapping("/{id}")
    public User user(@PathVariable("id") Long id) {
        Optional<User> userFind = Optional.ofNullable(userService.GetUserById(id));

        return userFind.orElse(null);
    }

    @PostMapping
    public User save(@RequestBody @Valid UserVM userVM) {

        return userService.SaveUser(mapperUtil.mapTo(userVM,User.class));
    }

    @PostMapping("/role")
    public Role saveRole(@RequestBody Role role) {
        return userService.SaveRole(role);
    }

    @GetMapping("/list")
    public List<User> list() {

        return userService.ListUsers();
    }

    @GetMapping("/findByEmail/{email}")
    public User findByEmail (@PathVariable("email") String email) {
        return userService.GetUsersByEmail(email);
    }
}
