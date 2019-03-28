package com.homebudget.homebudget.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.ExpenditureRepository;
import com.homebudget.homebudget.service.IncomeRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Utils;
import com.homebudget.homebudget.utils.Type;

@Controller
public class CategoryController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Autowired
	ExpenditureRepository expenditureRepository;
	
	@Autowired
	IncomeRepository incomeRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value = "/categories", method = RequestMethod.GET)
	public String categories(ModelMap model) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.EXPENDITURE, user);
		model.put("categories", categories);
		
		if (categories.size() > 0) {
			List<SubCategory> subCategories = subCategoryRepository.findByCategoryOrderByName(categories.get(0));
			model.put("subCategories", subCategories);
		}
		
		return "categories";
	}

	@RequestMapping(value = "/remove-category-{categoryId}", method = RequestMethod.GET)
	public String removeCategoryGet(ModelMap model, 
			@PathVariable(value = "categoryId") int categoryId) {
		
		Category category = categoryRepository.findById(categoryId);
		
		if (!category.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		List<SubCategory> subCategories = subCategoryRepository.findByCategoryOrderByName(category);
		
		model.put("isInUse", areSubCategoriesInUse(subCategories));
		
		return "remove-category";
	}
	
	@RequestMapping(value = "/remove-category-{categoryId}", method = RequestMethod.POST)
	public String removeCategoryPost(HttpServletRequest request, 
			@PathVariable(value = "categoryId") int categoryId) {
		
		Set<String> params = request.getParameterMap().keySet();
		Category category = categoryRepository.findById(categoryId);
		List<SubCategory> subCategories = subCategoryRepository.findByCategoryOrderByName(category);
		
		if (!category.getUser().getUsername().equals(Utils.getLoggedInUserName()) || 
				areSubCategoriesInUse(subCategories)) {
			return "forbidden";
		}
		
		if (params.contains("yes")) {
			
			for (SubCategory subCategory : subCategories) {
				subCategoryRepository.delete(subCategory);
			}
			
			categoryRepository.delete(category);
		}
		
		return "redirect:/categories";
	}
	
	private boolean areSubCategoriesInUse(List<SubCategory> subCategories) {
		
		for (SubCategory subCategory : subCategories) {
			if (isSubCategoryInUse(subCategory)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isSubCategoryInUse(SubCategory subCategory) {
		if (expenditureRepository.findBySubCategory(subCategory).size() > 0 || 
				incomeRepository.findBySubCategory(subCategory).size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
////////////////////////////////OLD
	
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
