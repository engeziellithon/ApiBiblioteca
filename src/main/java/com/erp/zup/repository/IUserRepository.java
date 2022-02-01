package com.erp.zup.repository;

import java.util.List;

import com.erp.zup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
    List<User> findByNameIgnoreCase(String name);
}
