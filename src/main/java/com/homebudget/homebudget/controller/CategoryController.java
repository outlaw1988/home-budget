package com.homebudget.homebudget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CategoryController {

	@RequestMapping(value = {"/add-category"}, method = RequestMethod.GET)
	public String addCategory() {
		return "add-category";
	}
	
}
