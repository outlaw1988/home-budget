package com.homebudget.homebudget.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.EmailServiceImpl;
import com.homebudget.homebudget.service.UserService;

@Controller
public class ForgotPasswordController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailServiceImpl emailService;
	
	@Value("${main.url}")
	private String mainUrl;
	
	@RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
	public String displayForgotPasswordPage() {
		return "forgot-password";
	}
	
	@RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
	public String forgotPasswordPost(HttpServletRequest request, ModelMap model) {
		
		String email = request.getParameter("email");
		
		List<User> users = userService.findUserByEmail(email);
		model.put("infoMessage", "Jeżeli email istnieje w bazie, wiadomość z linkiem "
				+ "resetującym hasło zostanie wysłana");
		
		if (users.size() == 0) {
			// TODO redirect
			return "forgot-password";
		}
		
		User user = users.get(0);
		user.setResetToken(UUID.randomUUID().toString());
		
		userService.saveUser(user, true);
		
		// Email message
		SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
		passwordResetEmail.setFrom("support@demo.com");
		passwordResetEmail.setTo(user.getEmail());
		passwordResetEmail.setSubject("Aplikacja domowy budżet - resetowanie hasła");
		passwordResetEmail.setText("Aby zresetować hasło kliknij w link poniżej:\n" + mainUrl
				+ "/reset?token=" + user.getResetToken());
		
		emailService.sendEmail(passwordResetEmail);
		
		return "forgot-password";
	}
	
	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public String displayResetPasswordPage(@RequestParam("token") String token, ModelMap model) {
		
		List<User> users = userService.findUserByResetToken(token);
		
		if (users.size() == 0) {
			model.put("invalidToken", true);
		} else {
			model.put("invalidToken", false);
		}
		
		return "reset-password";
	}
	
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public String resetPasswordPagePost(@RequestParam("token") String token, HttpServletRequest request, 
										ModelMap model) {
		
		List<User> users = userService.findUserByResetToken(token);
		
		if (users.size() == 0) {
			model.put("invalidToken", true);
		} else {
			model.put("invalidToken", false);
			User user = users.get(0);
			
			String password = request.getParameter("password");
			String passwordConfirm = request.getParameter("password-confirm");
			
			if (!password.equals(passwordConfirm)) {
				model.put("passwordError", "Hasła nie pasują do siebie");
				return "reset-password";
			}
			
			user.setPassword(password);
			user.setPasswordConfirm(passwordConfirm);
			
	        userService.saveUser(user, true);
	        
	        model.put("successMessage", "Hasło zostało poprawnie zmienione");
	        return "reset-password";
		}
		
		return "reset-password";
	}
	
}
