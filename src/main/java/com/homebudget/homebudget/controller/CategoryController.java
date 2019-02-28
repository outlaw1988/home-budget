package com.homebudget.homebudget.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Utils;

@Controller
public class CategoryController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Autowired
	UserRepository userRepository;

	@RequestMapping(value = {"/add-category"}, method = RequestMethod.GET)
	public String addCategory(ModelMap model) {
		
		model.addAttribute("subCategory", new SubCategory());
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<Category> categories = categoryRepository.findByUser(user);
		model.put("categories", categories);
		
		return "add-category";
	}
	
	@RequestMapping(value = "/add-category", method = RequestMethod.POST)
	public String addCategoryPost(ModelMap model, SubCategory subCategory, BindingResult result) {
		
		if (subCategory.getCategory() == null) {
			result.rejectValue("category", "error.category", "Wybierz kategorię");
		}
		
		if (result.hasErrors()) {
			User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
			List<Category> categories = categoryRepository.findByUser(user);
			model.put("categories", categories);
			
			return "add-category";
		}
		
		subCategoryRepository.save(subCategory);
		
		return "redirect:/index";
	}
	
	@RequestMapping(value = "/add-main-category", method = RequestMethod.GET)
	public String addMainCategory(ModelMap model) {
		
		model.addAttribute("category", new Category());
		
		return "add-main-category";
	}
	
	@RequestMapping(value = "/add-main-category", method = RequestMethod.POST)
	public String addMainCategoryPost(@Valid Category category) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		category.setUser(user);
		categoryRepository.save(category);
		
		return "redirect:/add-category";
	}

}
