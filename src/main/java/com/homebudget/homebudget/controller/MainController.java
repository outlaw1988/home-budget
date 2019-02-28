package com.homebudget.homebudget.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.utils.Utils;

@Controller
public class MainController {

	@Autowired
	MonthYearRepository monthYearRepository;
	
	@RequestMapping(value = {"/index", "/"}, method = RequestMethod.GET)
	public String index(HttpServletRequest request) {
		
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository);
		HttpSession session = request.getSession();
		session.setAttribute("username", Utils.getLoggedInUserName());
		
		return "index";
	}
	
}
