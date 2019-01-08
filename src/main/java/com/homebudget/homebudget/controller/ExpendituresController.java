package com.homebudget.homebudget.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.Expenditure;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.ExpenditureRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
import com.homebudget.homebudget.utils.Utils;

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

	@RequestMapping(value = "/expenditures", method = RequestMethod.GET)
	public String expendituresList(ModelMap model) {
		
		Utils.checkAndAddMonthYear(new Date(), monthYearRepository);
		
		List<Expenditure> expenditures = expenditureRepository.findAllByOrderByDateTimeDesc();
		model.put("expenditures", expenditures);
		
		return "expenditures";
	}
	
	@RequestMapping(value = "/add-expenditure", method = RequestMethod.GET)
	public String addExpenditure(ModelMap model) {
		
		model.addAttribute("expenditure", new Expenditure());
		model.put("currentDate", new Date());
		
		List<Category> categories = categoryRepository.findByType("wydatek");
		model.put("categories", categories);
		
		return "add-expenditure";
	}
	
	@RequestMapping(value = "/add-expenditure", method = RequestMethod.POST)
	public String addExpenditurePost(Expenditure expenditure) {
		
		MonthYear monthYear = Utils.checkAndAddMonthYear(expenditure.getDateTime(), monthYearRepository);
		
		expenditure.setMonthYear(monthYear);
		expenditureRepository.save(expenditure);
		
		return "redirect:/expenditures";
	}
	
	@RequestMapping(value = "/get-subcategories", method = RequestMethod.POST)
	public @ResponseBody List<SubCategory> getSubCategories(@RequestBody CategoryId categoryId) {
		
		Category category = categoryRepository.findById(Integer.parseInt(categoryId.categoryId));
		
		return subCategoryRepository.findByCategory(category);
	}
	
}

//Auxiliary classes
class CategoryId {
	public String categoryId;
}
