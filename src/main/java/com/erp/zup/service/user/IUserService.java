package com.erp.zup.service.user;
import com.erp.zup.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
    User CreateUser(User user);
    User GetUserById(Long id);
    List<User> GetUsersByName(String name);
    List<User> ListUsers();
}

