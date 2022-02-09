package com.erp.zup.service.auth;

import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthService {
    UserDetails AuthUserByEmail(String email);
}
