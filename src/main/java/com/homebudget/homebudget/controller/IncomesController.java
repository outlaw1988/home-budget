package com.homebudget.homebudget.controller;

import java.util.Date;
import java.util.List;

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
import com.homebudget.homebudget.model.Income;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.IncomeRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Utils;

@Controller
public class IncomesController {

	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Autowired
	IncomeRepository incomeRepository;
	
	@Autowired
	MonthYearRepository monthYearRepository;
	
	@Autowired
	UserRepository userRepository;
	
	private Date currentDate;

	@RequestMapping(value = "/incomes", method = RequestMethod.GET)
	public String incomesList(ModelMap model) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository, user);
		
		List<Income> incomes = incomeRepository.findByUserOrderByDateTimeDesc(user);
		model.put("incomes", incomes);
		
		return "incomes";
	}
	
	@RequestMapping(value = "/add-income", method = RequestMethod.GET)
	public String addIncome(ModelMap model) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		
		model.addAttribute("income", new Income());
		currentDate = new Date();
		model.put("currentDate", currentDate);
		
		List<Category> categories = categoryRepository.findByTypeAndUser("dochód", user);
		model.put("categories", categories);
		
		return "add-income";
	}
	
	@RequestMapping(value = "/add-income", method = RequestMethod.POST)
	public String addIncomePost(ModelMap model, @Valid Income income, BindingResult result) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		
		if (income.getSubCategory() == null) {
			result.rejectValue("subCategory", "error.subCategory", 
					"Kategoria/podkategoria nie może być pusta");
		}
		
		if (result.hasErrors()) {
			List<Category> categories = categoryRepository.findByTypeAndUser("dochód", user);
			model.put("categories", categories);
			model.put("currentDate", currentDate);
			return "add-income";
		}
		
		MonthYear monthYear = Utils.checkAndAddMonthYear(income.getDateTime(), monthYearRepository, user);
		
		income.setMonthYear(monthYear);
		income.setUser(user);
		incomeRepository.save(income);
		
		return "redirect:/incomes";
	}
	
	@RequestMapping(value = "/remove-income-{incomeId}", method = RequestMethod.GET)
	public String removeIncome(ModelMap model, 
			@PathVariable(value = "incomeId") int incomeId) {
		
		Income income = incomeRepository.findById(incomeId);
		
		if (!income.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		return "remove-income";
	}
	
	@RequestMapping(value = "/remove-income-{incomeId}", method = RequestMethod.POST)
	public String removeIncomePost(HttpServletRequest request, 
										@PathVariable(value = "incomeId") int incomeId) {
		
		java.util.Set<String> params = request.getParameterMap().keySet();
		Income income = incomeRepository.findById(incomeId);
		
		if (!income.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		if (params.contains("yes")) {
			incomeRepository.deleteById(incomeId);
		}
		
		return "redirect:/incomes";
	}
	
	@RequestMapping(value = "update-income-{incomeId}", method = RequestMethod.GET)
	public String updateIncomeGet(ModelMap model, @PathVariable(value = "incomeId") int incomeId) {
		
		Income income = incomeRepository.findById(incomeId);
		
		if (!income.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		model.addAttribute("income", income);
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<Category> categories = categoryRepository.findByTypeAndUser("dochód", user);
		model.put("categories", categories);
		
		List<SubCategory> subCategories = subCategoryRepository.findByCategory(income.getSubCategory().getCategory());
		model.put("subCategories", subCategories);
		
		return "update-income";
	}
	
	@RequestMapping(value = "update-income-{incomeId}", method = RequestMethod.POST)
	public String updateIncomePost(ModelMap model, @Valid Income income, BindingResult result) {
		
		if (!income.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		if (income.getSubCategory() == null) {
			result.rejectValue("subCategory", "error.subCategory", 
					"Kategoria/podkategoria nie może być pusta");
		}
		
		if (result.hasErrors()) {
			List<Category> categories = categoryRepository.findByTypeAndUser("dochód", 
					income.getUser());
			model.put("categories", categories);
			
			return "update-income";
		}
		
		MonthYear monthYear = Utils.checkAndAddMonthYear(income.getDateTime(), monthYearRepository, 
				income.getUser());
		income.setMonthYear(monthYear);
		
		incomeRepository.save(income);
		
		return "redirect:/incomes";
	}
	
	// get-subcategories taken from ExpendituresController

}
