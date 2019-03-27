package com.homebudget.homebudget.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.homebudget.homebudget.utils.Type;
import static com.homebudget.homebudget.utils.Utils.*;


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
	
	BigDecimal incomesSum;
	BigDecimal expendituresSum;
	
	@RequestMapping(value = "/main-table", method = RequestMethod.GET)
	public String mainTable(ModelMap model) {
		
		User user = userRepository.findByUsername(getLoggedInUserName()).get(0);
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
		incomesSum = sumUp(accumulatedIncomes);
		model.put("incomesSum", incomesSum);
		
		// Expenditures
		List<AccumulatedItem> accumulatedExpenditures = manageAccumulation(months.get(0), 
													yearsSorted.get(0), "expenditure");
		
		model.put("expenditures", accumulatedExpenditures);
		expendituresSum = sumUp(accumulatedExpenditures);
		model.put("expendituresSum", expendituresSum);
		
		BigDecimal diff = incomesSum.subtract(expendituresSum);
		model.put("diff", diff);
		
		return "main-table";
	}
	
	@RequestMapping(value = "/get-accumulated-incomes-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getIncomesTable(@RequestBody MonthYearRequest monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
				  								Integer.parseInt(monthYearReq.year), "income");
		this.incomesSum = sumUp(accumulatedItems);
		
		return new AccumulatedItemResponse(accumulatedItems, incomesSum);
	}
	
	@RequestMapping(value = "/get-accumulated-expenditures-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getExpendituresTable(@RequestBody MonthYearRequest 
																	monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
												Integer.parseInt(monthYearReq.year), "expenditure");
		this.expendituresSum = sumUp(accumulatedItems);

		return new AccumulatedItemResponse(accumulatedItems, expendituresSum);
	}
	
	@RequestMapping(value = "/get-summary-table", method = RequestMethod.POST)
	public @ResponseBody SummaryResponse getSummary() {
		return new SummaryResponse(incomesSum.subtract(expendituresSum));
	}
	
	private List<AccumulatedItem> manageAccumulation(int month, int year, String type) {
		User user = userRepository.findByUsername(getLoggedInUserName()).get(0);
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
				accumulatedItems.add(new AccumulatedItem(subCategory, sum, Type.INCOME));
			} else if (type.equals("expenditure")) {
				accumulatedItems.add(new AccumulatedItem(subCategory, sum, Type.EXPENDITURE));
			}
			
		}
		
		return accumulatedItems;
	}
	
}

// Auxiliary classes

class AccumulatedItemResponse {
	
	public List<AccumulatedItem> accumulatedItems;
	public BigDecimal sum;
	
	public AccumulatedItemResponse(List<AccumulatedItem> accumulatedItems, BigDecimal sum) {
		super();
		this.accumulatedItems = accumulatedItems;
		this.sum = sum;
	}
	
}

class SummaryResponse {
	
	public BigDecimal diffValue;
	
	public SummaryResponse(BigDecimal diffValue) {
		super();
		this.diffValue = diffValue;
	}
	
}
