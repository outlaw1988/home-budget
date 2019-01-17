package com.homebudget.homebudget.controller;

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
import com.homebudget.homebudget.model.AccumulatedItem;
import com.homebudget.homebudget.service.ExpenditureRepository;
import com.homebudget.homebudget.service.IncomeRepository;
import com.homebudget.homebudget.service.MonthYearRepository;


@Controller
public class MainTableController {

	@Autowired
	MonthYearRepository monthYearRepository;
	
	@Autowired
	IncomeRepository incomeRepository;
	
	@Autowired
	ExpenditureRepository expenditureRepository;
	
	@RequestMapping(value = "/main-table", method = RequestMethod.GET)
	public String mainTable(ModelMap model) {
		
		// TODO add users
		List<MonthYear> monthsYears = monthYearRepository.findAll();
		
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
		
		List<MonthYear> monthsYears = monthYearRepository.findAll();
		
		return getMonthsSortedDescForGivenYear(monthsYears, Integer.parseInt(year.year));
	}
	
	
	// TODO SUM!!!!
	
	@RequestMapping(value = "/get-incomes-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getIncomesTable(@RequestBody MonthYearRequest monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
				  								Integer.parseInt(monthYearReq.year), "income");
		float sum = sumUp(accumulatedItems);
		
		return new AccumulatedItemResponse(accumulatedItems, sum);
	}
	
	@RequestMapping(value = "/get-expenditures-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getExpendituresTable(@RequestBody MonthYearRequest 
																	monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
												Integer.parseInt(monthYearReq.year), "expenditure");
		float sum = sumUp(accumulatedItems);

		return new AccumulatedItemResponse(accumulatedItems, sum);
	}
	
	private List<AccumulatedItem> manageAccumulation(int month, int year, String type) {
		List<MonthYear> monthYear = monthYearRepository.findByMonthAndYear(month, year);
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
	
	private float sumUp(List<AccumulatedItem> accumulatedItems) {
		
		float sum = 0f;
		
		for (AccumulatedItem it : accumulatedItems) {
			sum += it.getSumValue();
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
			
			float sum = 0f;
			
			for (Item item : items) {
				if (subCategory.getId() == item.getSubCategory().getId()) {
					sum += item.getValue();
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
	public float sum;
	
	public AccumulatedItemResponse(List<AccumulatedItem> accumulatedItems, float sum) {
		super();
		this.accumulatedItems = accumulatedItems;
		this.sum = sum;
	}
	
}
