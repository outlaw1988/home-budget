package com.homebudget.homebudget.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Utils;

@Controller
public class MainController {

	@Autowired
	MonthYearRepository monthYearRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value = {"/index", "/"}, method = RequestMethod.GET)
	public String index(HttpServletRequest request) {
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		
		Utils.checkAndAddMonthYear(Utils.getCurrentWarsawTime(), monthYearRepository, user);
		HttpSession session = request.getSession();
		session.setAttribute("username", Utils.getLoggedInUserName());
		
		return "index";
	}
	
}
