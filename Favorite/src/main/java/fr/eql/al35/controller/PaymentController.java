package fr.eql.al35.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eql.al35.entity.Cart;
import fr.eql.al35.entity.Command;
import fr.eql.al35.entity.User;
import fr.eql.al35.service.AccountIService;
import fr.eql.al35.service.CommandIService;

@Controller
@SessionAttributes({"sessionFactAddress"})
public class PaymentController {

	@Autowired
	CommandIService cmdService;

	@Autowired
	AccountIService accountService;

	@GetMapping("/payment")
	public String displayPayment(Model model, HttpSession session) {
		Command command = new Command();
		model.addAttribute("command", command);
		return "payment";
	}

	@PostMapping("/newCommand") 
	public String createNewCommand(Model model, HttpSession session,
			@ModelAttribute("command") Command command) {
		Cart sessionCart = (Cart) session.getAttribute("sessionCart");
		User sessionUser = (User) session.getAttribute("sessionUser");
		
		command = cmdService.createCommand(sessionCart, command); //ajouter les données du panier
		
		command.setUser(sessionUser); 
		cmdService.saveUser(sessionUser); 
		
		command.setReference(writeReference(sessionUser, command));
		cmdService.saveCommand(command); //stocker en BDD command et addresses
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:home";
	}

	private String writeReference(User user, Command command) {
		StringBuilder reference = new StringBuilder();
		reference.append("CMD_");
		reference.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss")));
		reference.append("_Client_");
		reference.append(user.getId()); //a modif avec le n° Client en session
		return reference.toString();
	}

}
