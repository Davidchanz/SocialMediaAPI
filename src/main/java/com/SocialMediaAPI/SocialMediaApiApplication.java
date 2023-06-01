package com.SocialMediaAPI;

import com.SocialMediaAPI.configuration.RsaKeyProperties;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class SocialMediaApiApplication {

	public static void main(String[] args) {
		 SpringApplication.run(SocialMediaApiApplication.class, args);
	}
}
