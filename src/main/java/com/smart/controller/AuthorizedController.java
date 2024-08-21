package com.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AuthorizedController {

	@GetMapping("/index")
	public String dashboard() {
		
		System.out.println("===================");
		
		return "authorized/dashboard";
	}
}
