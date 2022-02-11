package com.erp.zup.service.user;

import com.erp.zup.domain.Role;
import com.erp.zup.domain.User;
import com.erp.zup.service.IServiceBase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface IUserService extends IServiceBase<User, Long> {
    User findUserByEmail(String email);
    Role createRole(Role role);
    void saveRoleToUser(String email, String roleName);
}



