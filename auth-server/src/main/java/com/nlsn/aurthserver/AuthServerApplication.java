package com.nlsn.aurthserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
public class AuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

	@Bean
	InMemoryUserDetailsManager inMemoryUserDetailsManager(){
		var userBuilder = User.withDefaultPasswordEncoder();

		return new InMemoryUserDetailsManager(
				userBuilder.roles("USER").username("nlsn").password("nlsn").build(),
				userBuilder.roles("USER", "ADMIN").username("raja").password("raja").build()
		);
	}

}
