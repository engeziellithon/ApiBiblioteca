package com.erp.zup.service.user;

import com.erp.zup.domain.User;
import com.erp.zup.repository.IUserRepository;
import com.erp.zup.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Override

    public User CreateUser(User user) {

        return userRepository.save(user);
    }

    @Override
    public User GetUserById(Long id) {
        Optional<User> userFind = userRepository.findById(id);
        return userFind.orElse(null);
    }

    @Override
    public List<User> GetUsersByName(String name) {
        return userRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<User> ListUsers() {
        return userRepository.findAll();
    }
}
