package com.erp.zup;

import com.erp.zup.domain.*;
import com.erp.zup.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ZupApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZupApplication.class, args);
	}

//	@Bean
//	CommandLineRunner run(UserService userService){
//		return  args -> {
//			userService.SaveRole(new Role("Admin"));
//			userService.SaveRole(new Role("User"));
//			userService.SaveRole(new Role("Manager"));
//
//			userService.SaveUser(new User("user@user.com","user","password", null));
//			userService.SaveUser(new User("admin@admin.com","admin","password", null));
//			userService.SaveUser(new User("manager@manager.com","manager","password", null));
//
//			userService.SaveRoleToUser("user@user.com","User");
//			userService.SaveRoleToUser("admin@admin.com","Admin");
//			userService.SaveRoleToUser("manager@manager.com","Manager");
//		};
//	}
}
