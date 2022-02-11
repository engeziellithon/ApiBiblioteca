package com.erp.zup.service;

import com.erp.zup.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServiceBase<T,ID> {
    T create(T entity);
    T update(T entity);
    void delete(ID id);

    User findById(ID id);
    Page<T> findAll(Pageable pageable);
}
