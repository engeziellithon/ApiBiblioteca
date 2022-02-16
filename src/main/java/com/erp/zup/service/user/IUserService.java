package com.erp.zup.service.user;


import com.erp.zup.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IUserService {
    User findUserByEmail(String email);
    Optional<User> create(User entity);
    Optional<User> update(User entity);
    void delete(Long id);
    Optional<User> findById(Long id);
    Page<User> findAll(Pageable pageable);
}



