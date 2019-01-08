package com.homebudget.homebudget.controller;

import java.util.Date;

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
	public String index() {
		
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository);
		
		return "index";
	}
	
}
