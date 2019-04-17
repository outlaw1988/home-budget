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
	
	// CATEGORIES
	
	@RequestMapping(value = "/add-category-{typeId}", method = RequestMethod.GET)
	public String addCategoryGet(ModelMap model, @PathVariable(value = "typeId") int typeId) {
		
		model.addAttribute("category", new Category());
		model.put("typeId", typeId);
		
		return "add-category";
	}
	
	@RequestMapping(value = "/add-category-{typeId}", method = RequestMethod.POST)
	public String addCategoryPost(ModelMap model, @Valid Category category, BindingResult result,
								  @PathVariable(value = "typeId") int typeId) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		
		if (category.getName().isEmpty()) {
			result.rejectValue("name", "error.name", "Nazwa kategorii nie może być pusta");
		}
		
		if (categoryRepository.findByUserAndNameAndType(user, category.getName(), 
				category.getType()).size() > 0) {
			result.rejectValue("name", "error.name", "Podana kategoria dla danego rodzaju już istnieje");
		}
		
		if (result.hasErrors()) {
			
			if (category.getType().equals(Type.EXPENDITURE)) model.put("typeId", 1);
			else if (category.getType().equals(Type.INCOME)) model.put("typeId", 2);
			
			return "add-category";
		}
		
		category.setUser(user);
		categoryRepository.save(category);
		
		return "redirect:/categories";
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
	
	@RequestMapping(value = "/update-category-{categoryId}", method = RequestMethod.GET)
	public String updateCategoryGet(ModelMap model, @PathVariable(value="categoryId") int categoryId) {
		
		Category category = categoryRepository.findById(categoryId);
		
		if (!category.getUser().getUsername().equals(Utils.getLoggedInUserName())) return "forbidden";
		
		model.addAttribute("category", category);
		
		return "update-category";
	}
	
	@RequestMapping(value = "/update-category-{categoryId}", method = RequestMethod.POST)
	public String updateCategoryPost(ModelMap model, @Valid Category category, BindingResult result, 
									 @PathVariable(value="categoryId") int categoryId) {
		
		if (!category.getUser().getUsername().equals(Utils.getLoggedInUserName())) return "forbidden";
		
		Category categoryIn = categoryRepository.findById(categoryId);
		
		if (!category.getName().equals(categoryIn.getName()) && 
			 categoryRepository.findByUserAndNameAndType(category.getUser(), category.getName(), 
					 									 category.getType()).size() > 0) {
			result.rejectValue("name", "error.name", "Podana kategoria dla danego rodzaju już istnieje");
		}
		
		if (result.hasErrors()) {
			return "update-category";
		}
		
		categoryRepository.save(category);
		
		return "redirect:/categories";
	}
	
	// SUBCATEGORIES
	
	@RequestMapping(value = "/remove-subcategory-{subCategoryId}", method = RequestMethod.GET)
	public String removeSubCategoryGet(ModelMap model, 
			@PathVariable(value = "subCategoryId") int subCategoryId) {
		
		SubCategory subCategory = subCategoryRepository.findById(subCategoryId);
		
		if (!subCategory.getCategory().getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		model.put("isInUse", isSubCategoryInUse(subCategory));
		
		return "remove-subcategory";
	}
	
	@RequestMapping(value = "/remove-subcategory-{subCategoryId}", method = RequestMethod.POST)
	public String removeSubCategoryPost(HttpServletRequest request, 
			@PathVariable(value = "subCategoryId") int subCategoryId) {
		
		SubCategory subCategory = subCategoryRepository.findById(subCategoryId);
		Set<String> params = request.getParameterMap().keySet();
		
		if (!subCategory.getCategory().getUser().getUsername().equals(Utils.getLoggedInUserName()) || 
				isSubCategoryInUse(subCategory)) {
			return "forbidden";
		}
		
		if (params.contains("yes")) {
			subCategoryRepository.delete(subCategory);
		}
		
		return "redirect:/categories";
	}
	
	@RequestMapping(value = "/add-subcategory-{categoryId}", method = RequestMethod.GET)
	public String addSubCategoryGet(ModelMap model, @PathVariable(value = "categoryId") int categoryId) {
		
		Category category = categoryRepository.findById(categoryId);
		
		if (!category.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		model.addAttribute("subCategory", new SubCategory(category));
		model.put("categoryName", category.getName());
		
		return "add-sub-category";
	}
	
	@RequestMapping(value = "/add-subcategory-{categoryId}", method = RequestMethod.POST)
	public String addSubCategoryPost(ModelMap model, @Valid SubCategory subCategory, 
				@PathVariable(value = "categoryId") int categoryId, BindingResult result) {
		
		Category category = categoryRepository.findById(categoryId);
		
		if (!category.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		if (subCategoryRepository.findByCategoryAndName(category, subCategory.getName()).size() > 0) {
			result.rejectValue("name", "error.name", "Podana podkategoria dla danej kategorii już istnieje");
		}
		
		if (result.hasErrors()) {
			model.put("categoryName", category.getName());
			return "add-sub-category";
		}
		
		subCategoryRepository.save(subCategory);
		
		return "redirect:/categories";
	}
	
	@RequestMapping(value = "/update-subcategory-{subCategoryId}", method = RequestMethod.GET)
	public String updateSubCategoryGet(ModelMap model, 
									   @PathVariable(value="subCategoryId") int subCategoryId) {
		
		SubCategory subCategory = subCategoryRepository.findById(subCategoryId);
		
		if (!subCategory.getCategory().getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		model.addAttribute("subCategory", subCategory);
		model.put("categoryName", subCategory.getCategory().getName());
		
		return "update-subcategory";
	}
	
	@RequestMapping(value = "/update-subcategory-{subCategoryId}", method = RequestMethod.POST)
	public String updateSubCategoryPost(ModelMap model, @Valid SubCategory subCategory,
			@PathVariable(value="subCategoryId") int subCategoryId, BindingResult result) {
		
		SubCategory subCategoryIn = subCategoryRepository.findById(subCategoryId);
		
		if (!subCategory.getCategory().getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		if (!subCategory.getName().equals(subCategoryIn.getName()) && 
			subCategoryRepository.findByCategoryAndName(subCategory.getCategory(), 
			subCategory.getName()).size() > 0) {
			
			result.rejectValue("name", "error.name", "Podana podkategoria dla danej kategorii już istnieje");
		}
			
		if (result.hasErrors()) {
			model.put("categoryName", subCategory.getCategory().getName());
			return "update-subcategory";
		}
		
		subCategoryRepository.save(subCategory);
		
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
	
}
