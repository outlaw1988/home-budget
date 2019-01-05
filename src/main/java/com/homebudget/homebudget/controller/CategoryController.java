package com.homebudget.homebudget.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;

@Controller
public class CategoryController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	SubCategoryRepository subCategoryRepository;

	@RequestMapping(value = {"/add-category"}, method = RequestMethod.GET)
	public String addCategory(ModelMap model) {
		
		model.addAttribute("subCategory", new SubCategory());
		
		List<Category> categories = categoryRepository.findAll();
		model.put("categories", categories);
		
		return "add-category";
	}
	
	@RequestMapping(value = "/add-category", method = RequestMethod.POST)
	public String addCategoryPost(@Valid SubCategory subCategory) {
		
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
		
		categoryRepository.save(category);
		
		return "redirect:/add-category";
	}

}
