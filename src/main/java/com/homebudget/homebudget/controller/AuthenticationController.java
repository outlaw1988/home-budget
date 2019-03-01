package com.homebudget.homebudget.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.UserService;


@Controller
public class AuthenticationController {
	
	@Autowired
	UserService userService;

	@RequestMapping(value="/registration", method=RequestMethod.GET)
    public String registration(ModelMap model){
		
		model.addAttribute("user", new User());
		
        return "registration";
    }
	
	@RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registrationPost(ModelMap model, @Valid User user, 
    		BindingResult bindingResult){
	
		int loginSize = userService.findUserByUsername(user.getUsername()).size();
		
		if (loginSize > 0) {
			bindingResult.rejectValue("username", "error.username",
                    "Użytkownik o takim loginie już istnieje");
		}
		
		int emailSize = userService.findUserByEmail(user.getEmail()).size();
		
		if (emailSize > 0) {
			bindingResult.rejectValue("email", "error.email",
                    "Podany email istnieje już w bazie");
		}
		
		if (!user.getPassword().equals(user.getPasswordConfirm())) {
			bindingResult.rejectValue("passwordConfirm", "error.passwordConfirm", 
					"Hasła nie pasują do siebie");
		}
		
		if (bindingResult.hasErrors()) {
					
			return "registration";
		}
		
		userService.saveUser(user, false);
		
		model.addAttribute("user", new User());
		model.put("successMessage", "Użytkownik został poprawnie zarejestrowany");
		
        return "registration";
    }
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request,
			HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		// removing cookie
		Cookie userNameCookieRemove = new Cookie("home-budget-login-cookie", "");
		userNameCookieRemove.setMaxAge(0);
		response.addCookie(userNameCookieRemove);
		return "redirect:/login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(@RequestParam(name="error",required=false) String error, 
						ModelMap model) {
		
		if(error != null) {
			model.put("credentialsError", "Niepoprawne dane logowania");
			return "login";
		}
		
		return "login";
	}
	
}
