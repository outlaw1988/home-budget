package com.homebudget.homebudget.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homebudget.homebudget.model.Item;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.model.AccumulatedItem;
import com.homebudget.homebudget.service.ExpenditureRepository;
import com.homebudget.homebudget.service.IncomeRepository;
import com.homebudget.homebudget.service.MonthYearRepository;
import com.homebudget.homebudget.service.UserRepository;
import com.homebudget.homebudget.utils.Utils;


@Controller
public class MainTableController {

	@Autowired
	MonthYearRepository monthYearRepository;
	
	@Autowired
	IncomeRepository incomeRepository;
	
	@Autowired
	ExpenditureRepository expenditureRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value = "/main-table", method = RequestMethod.GET)
	public String mainTable(ModelMap model) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<MonthYear> monthsYears = monthYearRepository.findByUser(user);
		
		List<Integer> yearsSorted = getYearsSortedDesc(monthsYears);
		//System.out.println("Years sorted: " + yearsSorted);
		
		List<Integer> months = getMonthsSortedDescForGivenYear(monthsYears, yearsSorted.get(0));
		//System.out.println("Months for top year: " + months);
		
		model.put("years", yearsSorted);
		model.put("months", months);
		
		// Incomes
		List<AccumulatedItem> accumulatedIncomes = manageAccumulation(months.get(0), 
													yearsSorted.get(0), "income");
		
		model.put("incomes", accumulatedIncomes);
		model.put("incomesSum", sumUp(accumulatedIncomes));
		
		// Expenditures
		List<AccumulatedItem> accumulatedExpenditures = manageAccumulation(months.get(0), 
													yearsSorted.get(0), "expenditure");
		
		model.put("expenditures", accumulatedExpenditures);
		model.put("expendituresSum", sumUp(accumulatedExpenditures));
		
		return "main-table";
	}
	
	@RequestMapping(value = "/change-year", method = RequestMethod.POST)
	public @ResponseBody List<Integer> changeYearAndGetMonths(@RequestBody Year year) {
		
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<MonthYear> monthsYears = monthYearRepository.findByUser(user);
		
		return getMonthsSortedDescForGivenYear(monthsYears, Integer.parseInt(year.year));
	}
	
	@RequestMapping(value = "/get-incomes-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getIncomesTable(@RequestBody MonthYearRequest monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
				  								Integer.parseInt(monthYearReq.year), "income");
		BigDecimal sum = sumUp(accumulatedItems);
		
		return new AccumulatedItemResponse(accumulatedItems, sum);
	}
	
	@RequestMapping(value = "/get-expenditures-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getExpendituresTable(@RequestBody MonthYearRequest 
																	monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
												Integer.parseInt(monthYearReq.year), "expenditure");
		BigDecimal sum = sumUp(accumulatedItems);

		return new AccumulatedItemResponse(accumulatedItems, sum);
	}
	
	private List<AccumulatedItem> manageAccumulation(int month, int year, String type) {
		User user = userRepository.findByUsername(Utils.getLoggedInUserName()).get(0);
		List<MonthYear> monthYear = monthYearRepository.findByMonthAndYearAndUser(month, year, user);
		List<? extends Item> items = null;
		
		if (type.equals("income")) {
			items = incomeRepository.findByMonthYear(monthYear.get(0));
		} else if (type.equals("expenditure")) {
			items = expenditureRepository.findByMonthYear(monthYear.get(0));
		}
		
		List<AccumulatedItem> accumulatedItems = generateAccumulatedItems(items, type);
		return accumulatedItems.stream()
				.sorted(Comparator.comparing(a -> a.getSubCategory().getCategory().getName()))
				.collect(Collectors.toList());
	}
	
	private BigDecimal sumUp(List<AccumulatedItem> accumulatedItems) {
		
		BigDecimal sum = new BigDecimal(0);
		
		for (AccumulatedItem it : accumulatedItems) {
			sum = sum.add(it.getSumValue());
		}
		
		return sum;
	}
	
	private List<AccumulatedItem> generateAccumulatedItems(List<? extends Item> items, String type) {
		
		List<AccumulatedItem> accumulatedItems = new ArrayList<>();
		Set<SubCategory> subCategories = new HashSet<>();
		
		for (Item item : items) {
			subCategories.add(item.getSubCategory());
		}
		
		for (SubCategory subCategory : subCategories) {
			
			BigDecimal sum = new BigDecimal(0);
			
			for (Item item : items) {
				if (subCategory.getId() == item.getSubCategory().getId()) {
					sum = sum.add(item.getValue());
				}
			}
			
			if (type.equals("income")) {
				accumulatedItems.add(new AccumulatedItem(subCategory, sum, AccumulatedItem.Type.INCOME));
			} else if (type.equals("expenditure")) {
				accumulatedItems.add(new AccumulatedItem(subCategory, sum, 
															AccumulatedItem.Type.EXPENDITURE));
			}
			
		}
		
		return accumulatedItems;
	}
	
	private List<Integer> getYearsSortedDesc(List<MonthYear> monthsYears) {
		
		Set<Integer> years = new HashSet<>();
		
		for (MonthYear monthYear : monthsYears) {
			years.add(monthYear.getYear());
		}
		
		List<Integer> yearsSorted = new ArrayList<Integer>(years);
		Collections.sort(yearsSorted, Collections.reverseOrder());
		
		return yearsSorted;
	}
	
	private List<Integer> getMonthsSortedDescForGivenYear(List<MonthYear> monthsYears, Integer year) {
		
		List<Integer> months = new ArrayList<Integer>();
		
		for (MonthYear monthYear : monthsYears) {
			if (monthYear.getYear() == year) {
				months.add(monthYear.getMonth());
			}
		}
		
		Collections.sort(months, Collections.reverseOrder());
		
		return months;
	}
}

// Auxiliary classes

class Year {
	public String year;
}


class MonthYearRequest {
	public String month;
	public String year;
}


class AccumulatedItemResponse {
	
	public List<AccumulatedItem> accumulatedItems;
	public BigDecimal sum;
	
	public AccumulatedItemResponse(List<AccumulatedItem> accumulatedItems, BigDecimal sum) {
		super();
		this.accumulatedItems = accumulatedItems;
		this.sum = sum;
	}
	
}
