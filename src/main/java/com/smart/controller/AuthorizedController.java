package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repo.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class AuthorizedController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * java.security.Principal
	 * This interface represents the abstract notion of a Principal, 
	 * which can be used to represent any entity, such as an individual, 
	 * a corporation, and a login id.
	 * Here we can get username(email Id for the user).
	 * 
	 * @ModelAttribute is responsible for run the annotated method(addCommonData()) for every handler
	 * of the class AuthorizedController.
	 */

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		//we can get all the details of user using this userName
		String userName = principal.getName();
		User userByUserName = userRepository.getUserByUserName(userName);
		model.addAttribute("user", userByUserName);

		//System.out.println(userByUserName);
	}


	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		/**
		 *  data for user will get by addCommonData method for every handler of the class AuthorizedController.
		 */
		model.addAttribute("title", "Dashboard - Smart Contact Manager");
		return "authorized/dashboard";
	}

	@GetMapping("/add-contact")
	public String openContactForm(Model model, Principal principal) {

		model.addAttribute("title", "AddContact - Smart Contact Manager");
		model.addAttribute("contact", new Contact());

		return "authorized/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,  
			Principal principal,Model model, HttpSession session) {

		try {
			String name = principal.getName();
			User userByUserName = userRepository.getUserByUserName(name);

			contact.setUser(userByUserName);
			userByUserName.getContacts().add(contact);
			
			if(!file.getContentType().matches("image/.*")) {
				throw new Exception("Problem with file type file");
				
			}else {
				String fileName = userByUserName.getId()+"_"+file.getOriginalFilename();
				contact.setImg(fileName);
				//To get fully Qualified file path
				File saveFile = new ClassPathResource("static/img").getFile();
				//
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				
				long copy = Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				if(copy == 0L) {
					throw new Exception("Problem while saving Image file :"+ fileName);
				}
			}

			// Save data into conact table via user table
			userRepository.save(userByUserName);
			
			//Success msg.
			session.setAttribute("message", new Message("Contact added Successfully", "success"));
			//model.addAttribute("session", session);

			System.out.println(contact);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			if(!file.getContentType().matches("image/.*")) {
				session.setAttribute("message", new Message("File type is not valid, Please use jpge/png format.", "danger"));
			}else {
				session.setAttribute("message", new Message("Something went wrong", "danger"));
			}
			//model.addAttribute("session", session);
		}
		//we need to redirect it to ViewContacts page after successfully add the contact.
		return "authorized/add_contact_form";
	}

}
