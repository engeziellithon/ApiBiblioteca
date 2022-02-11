package com.erp.zup.repository;

import com.erp.zup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String email);
}
