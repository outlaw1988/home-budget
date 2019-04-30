package com.homebudget.homebudget.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.homebudget.homebudget.model.Expenditure;
import com.homebudget.homebudget.model.Income;
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
	
	private BigDecimal incomesSum;
	private BigDecimal incomesAverage;
	
	private BigDecimal expendituresSum;
	private BigDecimal expendituresAverage;
	
	private MonthYear currMonthYear;
	private User user;
	
	@RequestMapping(value = "/main-table", method = RequestMethod.GET)
	public String mainTable(ModelMap model) {
		
		user = userRepository.findByUsername(getLoggedInUserName()).get(0);
		currMonthYear = checkAndAddMonthYear(getCurrentWarsawTime(), monthYearRepository, user);
		
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
		
		incomesAverage = computeAverage(incomeRepository.findByUserOrderByDateTimeDesc(user));
		model.put("incomesAverage", incomesAverage);
		
		// Expenditures
		List<AccumulatedItem> accumulatedExpenditures = manageAccumulation(months.get(0), 
													yearsSorted.get(0), "expenditure");
		
		model.put("expenditures", accumulatedExpenditures);
		expendituresSum = sumUp(accumulatedExpenditures);
		model.put("expendituresSum", expendituresSum);
		
		expendituresAverage = computeAverage(expenditureRepository.findByUserOrderByDateTimeDesc(user));
		model.put("expendituresAverage", expendituresAverage);
		
		BigDecimal diff = incomesSum.subtract(expendituresSum);
		model.put("diff", diff);
		model.put("diffAverage", incomesAverage.subtract(expendituresAverage));
		
		return "main-table";
	}
	
	@RequestMapping(value = "/get-accumulated-incomes-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getIncomesTable(@RequestBody MonthYearRequest monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
				  								Integer.parseInt(monthYearReq.year), "income");
		this.incomesSum = sumUp(accumulatedItems);
		
		return new AccumulatedItemResponse(accumulatedItems, incomesSum, incomesAverage);
	}
	
	@RequestMapping(value = "/get-accumulated-expenditures-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getExpendituresTable(@RequestBody MonthYearRequest 
																	monthYearReq) {
		
		List<AccumulatedItem> accumulatedItems = manageAccumulation(Integer.parseInt(monthYearReq.month), 
												Integer.parseInt(monthYearReq.year), "expenditure");
		this.expendituresSum = sumUp(accumulatedItems);

		return new AccumulatedItemResponse(accumulatedItems, expendituresSum, expendituresAverage);
	}
	
	@RequestMapping(value = "/get-summary-table", method = RequestMethod.POST)
	public @ResponseBody SummaryResponse getSummary() {
		return new SummaryResponse(incomesSum.subtract(expendituresSum), 
				                   incomesAverage.subtract(expendituresAverage));
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
		
		//accumulatedItems.stream().forEach(System.out::println);
		
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
				List<Income> incomes = incomeRepository.findBySubCategory(subCategory);
				BigDecimal average = computeAverage(incomes);
				accumulatedItems.add(new AccumulatedItem(subCategory, sum, Type.INCOME, average));
			} else if (type.equals("expenditure")) {
				List<Expenditure> expenditures = expenditureRepository.findBySubCategory(subCategory);
				BigDecimal average = computeAverage(expenditures);
				accumulatedItems.add(new AccumulatedItem(subCategory, sum, Type.EXPENDITURE, average));
			}
			
		}
		
		return accumulatedItems;
	}
	
	private BigDecimal computeAverage(List<? extends Item> items) {
		
		BigDecimal sum = items.stream()
				.filter(it -> (it.getMonthYear().getYear() < currMonthYear.getYear()) ||
							  ((it.getMonthYear().getYear() == currMonthYear.getYear()) && 
							   (it.getMonthYear().getMonth() < currMonthYear.getMonth())))
				.map(it -> it.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		int numOfFinishedMonths = getNumberOfFinishedMonths(monthYearRepository.findByUser(user));
		
		if (numOfFinishedMonths == 0) return new BigDecimal(0);
		
		return sum.divide(new BigDecimal(numOfFinishedMonths)).setScale(2, RoundingMode.CEILING);
	}
	
}

// Auxiliary classes

class AccumulatedItemResponse {
	
	public List<AccumulatedItem> accumulatedItems;
	public BigDecimal sum;
	public BigDecimal average;
	
	public AccumulatedItemResponse(List<AccumulatedItem> accumulatedItems, BigDecimal sum, 
							       BigDecimal average) {
		super();
		this.accumulatedItems = accumulatedItems;
		this.sum = sum;
		this.average = average;
	}
	
}

class SummaryResponse {
	
	public BigDecimal diffValue;
	public BigDecimal average;
	
	public SummaryResponse(BigDecimal diffValue, BigDecimal average) {
		super();
		this.diffValue = diffValue;
		this.average = average;
	}
	
}
