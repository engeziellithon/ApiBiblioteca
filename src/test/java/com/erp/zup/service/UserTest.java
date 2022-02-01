package com.erp.zup.service;

import com.erp.zup.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;


public class UserTest {

    @Test
    public void checkEmail() {
        User user = new User();

        Assert.isTrue(user.IsValid(),"Email invalido");

       
    }
}
