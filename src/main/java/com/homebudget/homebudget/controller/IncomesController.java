package com.homebudget.homebudget.controller;

import static com.homebudget.homebudget.utils.Utils.getMonthsSortedDescForGivenYear;
import static com.homebudget.homebudget.utils.Utils.getYearsSortedDesc;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.Income;
import com.homebudget.homebudget.model.Item;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.IncomeRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Type;
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
		List<MonthYear> monthsYears = monthYearRepository.findByUser(user);
		List<Integer> yearsSorted = getYearsSortedDesc(monthsYears);
		List<Integer> months = getMonthsSortedDescForGivenYear(monthsYears, yearsSorted.get(0));
		
		model.put("years", yearsSorted);
		model.put("months", months);
		
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(months.get(0), 
				yearsSorted.get(0), user).get(0);
		
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository, user);
		
		List<Income> incomes = incomeRepository.findByMonthYearOrderByDateTimeDesc(monthYear);
		model.put("incomes", incomes);
		
		return "incomes";
	}
	
	@RequestMapping(value = "/add-income", method = RequestMethod.GET)
	public String addIncome(ModelMap model) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		
		model.addAttribute("income", new Income());
		currentDate = new Date();
		model.put("currentDate", currentDate);
		
		List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.INCOME, 
																					user);
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
			List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.INCOME, 
																						user);
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
		List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.INCOME, user);
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
			List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.INCOME, 
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get-incomes-table", method = RequestMethod.POST)
	public @ResponseBody ItemResponse getExpendituresTable(@RequestBody MonthYearRequest 
																	monthYearReq) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(Integer.parseInt(monthYearReq.month), 
				Integer.parseInt(monthYearReq.year), user).get(0);
		
		List<? extends Item> incomes = incomeRepository.findByMonthYearOrderByDateTimeDesc(monthYear);
		return new ItemResponse((List<Item>) incomes);
	}

}
