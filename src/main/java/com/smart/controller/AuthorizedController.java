package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.entities.User;
import com.smart.repo.UserRepository;

@Controller
@RequestMapping("/user")
public class AuthorizedController {
	
	@Autowired
	private UserRepository userRepository;

	/**
	 * java.security.Principal
	 * This interface represents the abstract notion of a Principal, 
	 * whichcan be used to represent any entity, such as an individual, 
	 * acorporation, and a login id.
	 * Here we can get username(email Id for the user).
	 */
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		//we can get all the details of user using this userName
		String userName = principal.getName();
		User userByUserName = userRepository.getUserByUserName(userName);
		model.addAttribute("user", userByUserName);
		
		//System.out.println(userByUserName);
		
		return "authorized/dashboard";
	}
}
