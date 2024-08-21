package com.smart.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.smart.entities.User;
import com.smart.repo.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;

	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetching details from database
		
		User userByUserName = userRepository.getUserByUserName(username);
		
		if(userByUserName == null) {
			throw new UsernameNotFoundException("Could not found user"); 
		}
		
		System.out.println(userByUserName.getEmail());
		System.out.println(userByUserName.getRole());

		CustomUserDetails customUserDetails = new CustomUserDetails(userByUserName);
		
		return customUserDetails; 
	}

}
