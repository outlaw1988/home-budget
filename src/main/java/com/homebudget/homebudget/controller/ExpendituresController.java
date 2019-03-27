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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.Expenditure;
import com.homebudget.homebudget.model.Item;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.ExpenditureRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Type;
import com.homebudget.homebudget.utils.Utils;

import static com.homebudget.homebudget.utils.Utils.*;

@Controller
public class ExpendituresController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Autowired
	ExpenditureRepository expenditureRepository;
	
	@Autowired
	MonthYearRepository monthYearRepository;
	
	@Autowired
	UserRepository userRepository;
	
	private Date currentDate;

	@RequestMapping(value = "/expenditures", method = RequestMethod.GET)
	public String expendituresList(ModelMap model) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<MonthYear> monthsYears = monthYearRepository.findByUser(user);
		List<Integer> yearsSorted = getYearsSortedDesc(monthsYears);
		List<Integer> months = getMonthsSortedDescForGivenYear(monthsYears, yearsSorted.get(0));
		
		model.put("years", yearsSorted);
		model.put("months", months);
		
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(months.get(0), 
				yearsSorted.get(0), user).get(0);
		
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository, user);
		
		List<Expenditure> expenditures = expenditureRepository.findByMonthYearOrderByDateTimeDesc(
				monthYear);
		model.put("expenditures", expenditures);
		
		return "expenditures";
	}
	
	@RequestMapping(value = "/add-expenditure", method = RequestMethod.GET)
	public String addExpenditure(ModelMap model) {
		
		model.addAttribute("expenditure", new Expenditure());
		currentDate = new Date();
		model.put("currentDate", currentDate);
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		
		List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.EXPENDITURE, user);
		model.put("categories", categories);
		
		return "add-expenditure";
	}
	
	@RequestMapping(value = "/add-expenditure", method = RequestMethod.POST)
	public String addExpenditurePost(ModelMap model, @Valid Expenditure expenditure, 
			BindingResult result) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		
		if (expenditure.getSubCategory() == null) {
			result.rejectValue("subCategory", "error.subCategory", 
					"Kategoria/podkategoria nie może być pusta");
		}
		
		if (result.hasErrors()) {
			List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.EXPENDITURE, 
																						user);
			model.put("categories", categories);
			model.put("currentDate", currentDate);
			return "add-expenditure";
		}
		
		MonthYear monthYear = Utils.checkAndAddMonthYear(expenditure.getDateTime(), monthYearRepository, 
				user);
		
		expenditure.setMonthYear(monthYear);
		expenditure.setUser(user);
		expenditureRepository.save(expenditure);
		
		return "redirect:/expenditures";
	}
	
	@RequestMapping(value = "/remove-expenditure-{expenditureId}", method = RequestMethod.GET)
	public String removeExpenditure(ModelMap model, 
			@PathVariable(value = "expenditureId") int expenditureId) {
		
		Expenditure expenditure = expenditureRepository.findById(expenditureId);
		
		if (!expenditure.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		return "remove-expenditure";
	}
	
	@RequestMapping(value = "/remove-expenditure-{expenditureId}", method = RequestMethod.POST)
	public String removeExpenditurePost(HttpServletRequest request, 
										@PathVariable(value = "expenditureId") int expenditureId) {
		
		java.util.Set<String> params = request.getParameterMap().keySet();
		Expenditure expenditure = expenditureRepository.findById(expenditureId);
		
		if (!expenditure.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		if (params.contains("yes")) {
			expenditureRepository.deleteById(expenditureId);
		}
		
		return "redirect:/expenditures";
	}
	
	@RequestMapping(value = "/update-expenditure-{expenditureId}", method = RequestMethod.GET)
	public String updateExpenditureGet(ModelMap model, 
			@PathVariable(value="expenditureId") int expenditureId) {
		
		Expenditure expenditure = expenditureRepository.findById(expenditureId);
		
		if (!expenditure.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		model.addAttribute("expenditure", expenditure);
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.EXPENDITURE, user);
		model.put("categories", categories);
		
		List<SubCategory> subCategories = subCategoryRepository.findByCategoryOrderByName(
											expenditure.getSubCategory().getCategory());
		model.put("subCategories", subCategories);
		
		return "update-expenditure";
	}
	
	@RequestMapping(value = "/update-expenditure-{expenditureId}", method = RequestMethod.POST)
	public String updateExpenditurePost(ModelMap model, @Valid Expenditure expenditure, 
			BindingResult result) {
		
		if (!expenditure.getUser().getUsername().equals(Utils.getLoggedInUserName())) {
			return "forbidden";
		}
		
		if (expenditure.getSubCategory() == null) {
			result.rejectValue("subCategory", "error.subCategory", 
					"Kategoria/podkategoria nie może być pusta");
		}
		
		if (result.hasErrors()) {
			List<Category> categories = categoryRepository.findByTypeAndUserOrderByName(Type.EXPENDITURE, 
																				expenditure.getUser());
			model.put("categories", categories);
			
			return "update-expenditure";
		}
		
		MonthYear monthYear = Utils.checkAndAddMonthYear(expenditure.getDateTime(), monthYearRepository, 
				expenditure.getUser());
		expenditure.setMonthYear(monthYear);
		
		expenditureRepository.save(expenditure);
		
		return "redirect:/expenditures";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get-expenditures-table", method = RequestMethod.POST)
	public @ResponseBody ItemResponse getExpendituresTable(@RequestBody MonthYearRequest 
																	monthYearReq) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(Integer.parseInt(monthYearReq.month), 
				Integer.parseInt(monthYearReq.year), user).get(0);
		
		List<? extends Item> expenditures = expenditureRepository.findByMonthYearOrderByDateTimeDesc(
											monthYear);
		return new ItemResponse((List<Item>) expenditures);
	}
	
}
