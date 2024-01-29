package com.nlsn.aurthserver;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class AuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

//	@Bean
//	InMemoryUserDetailsManager inMemoryUserDetailsManager(){
//		var userBuilder = User.withDefaultPasswordEncoder();
//
//		return new InMemoryUserDetailsManager(
//				userBuilder.roles("USER").username("nlsn").password("nlsn").build(),
//				userBuilder.roles("USER", "ADMIN").username("raja").password("raja").build()
//		);
//	}

}

@Configuration
class UserConfiguration{

	@Bean
	JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource){
		return new JdbcUserDetailsManager(dataSource);
	}

	@Bean
	ApplicationRunner userRunner(UserDetailsManager userDetailsManager){
		return args -> {
				var builder = User.withDefaultPasswordEncoder();
				var users = Map.of("nlsn", "nlsn", "raja", "raja");
				users.forEach((username, password)->{
					var user = builder.roles("USER").username(username).password(password).build();
					userDetailsManager.createUser(user);
				});
		};
	}

}

@Configuration
class ClientsConfiguration {

	@Bean
	RegisteredClientRepository registeredClientRepository (JdbcTemplate jdbcTemplate){
		return new JdbcRegisteredClientRepository(jdbcTemplate);
	}

	@Bean
	ApplicationRunner applicationRunner(RegisteredClientRepository registeredClientRepository){
		return args -> {
			var clientId = "crm";
			if(registeredClientRepository.findByClientId(clientId) == null){
				registeredClientRepository.save(
						RegisteredClient
								.withId(UUID.randomUUID().toString())
								.clientId(clientId)
								.clientSecret("{noop}crm")
								.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
								.authorizationGrantTypes(authorizationGrantTypes -> {
									authorizationGrantTypes.addAll(Set.of(
											AuthorizationGrantType.CLIENT_CREDENTIALS,
											AuthorizationGrantType.AUTHORIZATION_CODE,
											AuthorizationGrantType.REFRESH_TOKEN
									));
								})
								.redirectUri("http://127.0.0.1:8082/login/oauth2/code/spring")
								.scopes(scopes -> scopes.addAll(Set.of("user.read", "user.write", OidcScopes.OPENID)))
								.build()
				);
			}
		};
	}

}
