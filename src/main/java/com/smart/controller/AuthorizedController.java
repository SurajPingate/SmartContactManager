package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repo.ContactRepository;
import com.smart.repo.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class AuthorizedController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;

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

			if(file.isEmpty()) {
				contact.setImg("contact.png");
			}else if(!file.getContentType().matches("image/.*")) {
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

			// Save data into contact table via user table
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
			return "authorized/add_contact_form";
		}
		//we need to redirect it to ViewContacts page after successfully add the contact.
		return "redirect:/user/show-contacts/0";

	}

	//Show contacts Handler
	//Pagination code (Added in pagination branch)
	//per page = 5[n]
	//current page = 1[page]
	@GetMapping("/show-contacts/{page}")
	public String showContracts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contact - Smart Contact Manager");

		/*
		 * String userName = principal.getName(); 
		 * User user = userRepository.getUserByUserName(userName); 
		 * List<Contact> allContacts = user.getContacts();
		 */

		// We get the data from contactRepository
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contactsByUserId = contactRepository.findContactsByUserId(user.getId(), pageable);

		if(contactsByUserId.isEmpty()) {
			model.addAttribute("message", new Message("Your Contact List is Empty, Please add new contacts.", "warning"));
		}else {
			model.addAttribute("contacts", contactsByUserId);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", contactsByUserId.getTotalPages());
		}
		return "authorized/show_contacts";
	}

	//Showing specific contact details
	@GetMapping("/contact/{id}")
	public String shoeContactDetails(@PathVariable("id") int id, Model model) {

		System.out.println(id);
		//to get actual obj from optional use optional.get method.
		//Optional<Contact> contactById = contactRepository.findById(id);
		//Contact contact = contactById.get();

		Contact contactById = contactRepository.findById(id).get();

		model.addAttribute("contact",contactById);

		return "authorized/contact_details";
	}


}
