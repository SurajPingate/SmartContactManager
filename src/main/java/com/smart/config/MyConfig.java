package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig{
	
	@Bean 
	public UserDetailsService getuserDetailsService() {
		return new UserDetailsServiceImpl(); 
	}

	@Bean 
	public PasswordEncoder PasswordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() { 
		
		// setting the authentication provider to user authentication from db table
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(getuserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(PasswordEncoder());
		
		System.out.println(daoAuthenticationProvider);
		
		return daoAuthenticationProvider;
		
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager(); 
	}
	
	@Bean 
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
		/**
		 * Spring Security's default configuration includes CSRF (Cross-Site Request Forgery) protection, 
		which requires a CSRF token for state-changing requests (such as POST requests). 
		Since curl doesn’t include the CSRF token by default, this could be causing the 403 Forbidden error.
		Solution:
		You can disable CSRF protection for testing purposes (not recommended for production):
		 */
		
		http
		.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/user/**").hasRole("USER")
				.requestMatchers("/**","/login")
				.permitAll()
				.anyRequest()
				.authenticated())
		.formLogin(formLogin -> formLogin
				. loginPage("/signin")
				.defaultSuccessUrl("/admin/index")
				.permitAll() )
		.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/signin")
				.permitAll() )
		.authenticationProvider(daoAuthenticationProvider()); //Set the custom authentication provider

		DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
		System.out.println(defaultSecurityFilterChain.toString());
		return defaultSecurityFilterChain; 
	}
}
