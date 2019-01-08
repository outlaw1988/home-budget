package com.homebudget.homebudget.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.Income;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.IncomeRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
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

	@RequestMapping(value = "/incomes", method = RequestMethod.GET)
	public String incomesList(ModelMap model) {
		
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository);
		
		List<Income> incomes = incomeRepository.findAllByOrderByDateTimeDesc();
		model.put("incomes", incomes);
		
		return "incomes";
	}
	
	@RequestMapping(value = "/add-income", method = RequestMethod.GET)
	public String addIncome(ModelMap model) {
		
		model.addAttribute("income", new Income());
		model.put("currentDate", new Date());
		
		List<Category> categories = categoryRepository.findByType("dochód");
		model.put("categories", categories);
		
		return "add-income";
	}
	
	@RequestMapping(value = "/add-income", method = RequestMethod.POST)
	public String addIncomePost(Income income) {
		
		MonthYear monthYear = Utils.checkAndAddMonthYear(income.getDateTime(), monthYearRepository);
				
		income.setMonthYear(monthYear);
		incomeRepository.save(income);
		
		return "redirect:/incomes";
	}
	
	// get-subcategories taken from ExpendituresController

}
