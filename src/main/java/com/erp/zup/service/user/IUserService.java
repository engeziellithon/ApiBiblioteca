package com.erp.zup.service.user;
import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
    User SaveUser(User user);
    User GetUserById(Long id);
    UserDetails AuthUserByEmail(String email);
    Role SaveRole(Role role);
    void SaveRoleToUser(String email,String roleName);
    User GetUsersByEmail(String email);
    List<User> ListUsers();
}

