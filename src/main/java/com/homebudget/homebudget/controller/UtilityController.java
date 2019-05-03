package com.homebudget.homebudget.controller;

import static com.homebudget.homebudget.utils.Utils.getLoggedInUserName;
import static com.homebudget.homebudget.utils.Utils.getMonthsSortedDescForGivenYear;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.Item;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.CategoryRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.SubCategoryRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Type;

@Controller
public class UtilityController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MonthYearRepository monthYearRepository;
	
	@RequestMapping(value = "/change-type-get-categories", method = RequestMethod.POST)
	public @ResponseBody List<Category> getCategoriesByType(@RequestBody TypeRequest type) {

		Type enumType = null;
		User user = userRepository.findByUsername(getLoggedInUserName()).get(0);
		
		if (type.type.equals("income")) {
			enumType = Type.INCOME;
		} else if (type.type.equals("expenditure")) {
			enumType = Type.EXPENDITURE;
		}
		
		return categoryRepository.findByTypeAndUserOrderByName(enumType, user);
	}

	@RequestMapping(value = "/get-subcategories", method = RequestMethod.POST)
	public @ResponseBody List<SubCategory> getSubCategories(@RequestBody CategoryId categoryId) {
		
		Category category = categoryRepository.findById(Integer.parseInt(categoryId.categoryId));
		
		return subCategoryRepository.findByCategoryOrderByName(category);
	}
	
	@RequestMapping(value = "/change-year", method = RequestMethod.POST)
	public @ResponseBody List<Integer> changeYearAndGetMonths(@RequestBody Year year) {
		
		User user = userRepository.findByUsername(getLoggedInUserName()).get(0);
		
		List<MonthYear> monthsYears = monthYearRepository.findByUser(user);
		return getMonthsSortedDescForGivenYear(monthsYears, Integer.parseInt(year.year));
	}
	
}

//Auxiliary classes

// Request classes
class TypeRequest {
	public String type;
}

class CategoryId {
	public String categoryId;
}

class Year {
	public String year;
}

class MonthYearRequest {
	public String month;
	public String year;
}

class DonationRequest {
	public String rate;
	public String month;
	public String year;
}

// Response classes
class ItemResponse {
	
	public List<Item> items;
	public String donationRate;
	
	public ItemResponse(List<Item> items) {
		this.items = items;
		this.donationRate = null;
	}
	
	public ItemResponse(List<Item> items, String rate) {
		this.items = items;
		this.donationRate = rate;
	}
	
}
