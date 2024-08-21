package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.smart.entities.User;
import com.smart.repo.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class UserController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup/")
	public String register(Model model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult results, Model model, HttpSession session) {
		
		try {
			
			if(results.hasErrors()) {
				System.out.println("ERROR "+results.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_ADMIN");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			User result = userRepository.save(user);
			System.out.println(result);
			model.addAttribute("user",new User());
			model.addAttribute("session", session);
			model.addAttribute("message", "Signup successful!");
			model.addAttribute("messageType", "success");
			
		//Old method	//session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
			return "signup";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			model.addAttribute("message", "Signup failed. Please try again.");
            model.addAttribute("messageType", "error");
			return "signup";
		}
	}
	
	@GetMapping("/signin")
	public String signIn(Model model) {
		model.addAttribute("title","Login - Smart Contact Manager");
		//model.addAttribute("user", new User());
		return "login";
	}
	
}
