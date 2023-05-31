package com.SocialMediaAPI;

import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SocialMediaApiApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(SocialMediaApiApplication.class, args);

		//createUser(context);
	}

	private static void createUser(ConfigurableApplicationContext context) {
		var userService = context.getBean(UserService.class);

		User user = new User();
		user.setUsername("GN");
		user.setPassword("123");
		userService.saveUser(user);
	}

}
