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
import com.homebudget.homebudget.model.Donation;
import com.homebudget.homebudget.model.Expenditure;
import com.homebudget.homebudget.model.Income;
import com.homebudget.homebudget.service.DonationRepository;
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
	
	@Autowired
	DonationRepository donationRepository;
	
	private BigDecimal incomesGrossSum;
	private BigDecimal incomesGrossAverage;
	
	private BigDecimal incomesNetSum;
	private BigDecimal incomesNetAverage;
	
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
		List<Integer> months = getMonthsSortedDescForGivenYear(monthsYears, yearsSorted.get(0));
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(months.get(0), 
				yearsSorted.get(0), user);
		
		model.put("years", yearsSorted);
		model.put("months", months);
		
		// Incomes gross
		List<AccumulatedItem> accumulatedIncomes = manageAccumulation(monthYear, "income");
		model.put("incomes", accumulatedIncomes);
		incomesGrossSum = sumUpAccumulatedItems(accumulatedIncomes);
		model.put("incomesGrossSum", incomesGrossSum);
		incomesGrossAverage = computeAverage(incomeRepository.findByUserOrderByDateTimeDesc(user));
		model.put("incomesGrossAverage", incomesGrossAverage);
		
		// Donations
		List<Donation> donations = donationRepository.findByMonthYearOrderByDateTimeDesc(monthYear);
		BigDecimal donationsSum = sumUpItems(donations);
		model.put("donationsSum", donationsSum);
		BigDecimal donationsAverage = computeAverage(donationRepository.findByUser(user));
		model.put("donationsAverage", donationsAverage);
		
		// Incomes net
		incomesNetSum = incomesGrossSum.subtract(donationsSum);
		model.put("incomesNetSum", incomesNetSum);
		incomesNetAverage = incomesGrossAverage.subtract(donationsAverage);
		model.put("incomesNetAverage", incomesNetAverage);
		
		// Expenditures
		List<AccumulatedItem> accumulatedExpenditures = manageAccumulation(monthYear, "expenditure");
		
		model.put("expenditures", accumulatedExpenditures);
		expendituresSum = sumUpAccumulatedItems(accumulatedExpenditures);
		model.put("expendituresSum", expendituresSum);
		
		expendituresAverage = computeAverage(expenditureRepository.findByUserOrderByDateTimeDesc(user));
		model.put("expendituresAverage", expendituresAverage);
		
		BigDecimal diff = incomesNetSum.subtract(expendituresSum);
		model.put("diff", diff);
		model.put("diffAverage", incomesNetAverage.subtract(expendituresAverage));
		
		return "main-table";
	}
	
	@RequestMapping(value = "/get-accumulated-incomes-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedIncomesResponse getIncomesTable(@RequestBody MonthYearRequest 
			monthYearReq) {
		
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(
				Integer.parseInt(monthYearReq.month), 
				Integer.parseInt(monthYearReq.year), user);
		List<AccumulatedItem> accumulatedItems = manageAccumulation(monthYear, "income");
		this.incomesGrossSum = sumUpAccumulatedItems(accumulatedItems);
		
		List<Donation> donations = donationRepository.findByMonthYearOrderByDateTimeDesc(monthYear);
		BigDecimal donationsSum = sumUpItems(donations);
		BigDecimal donationsAverage = computeAverage(donationRepository.findByUser(user));
		
		incomesNetSum = incomesGrossSum.subtract(donationsSum);
		incomesNetAverage = incomesGrossAverage.subtract(donationsAverage);
		
		return new AccumulatedIncomesResponse()
				.withAccumulatedItems(accumulatedItems)
				.withGrossSum(incomesGrossSum)
				.withGrossAverage(incomesGrossAverage)
				.withDonationsSum(donationsSum)
				.withDonationsAverage(donationsAverage)
				.withNetSum(incomesNetSum)
				.withNetAverage(incomesNetAverage);
	}
	
	@RequestMapping(value = "/get-accumulated-expenditures-table", method = RequestMethod.POST)
	public @ResponseBody AccumulatedItemResponse getExpendituresTable(@RequestBody MonthYearRequest 
																	monthYearReq) {
		
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(
				Integer.parseInt(monthYearReq.month), 
				Integer.parseInt(monthYearReq.year), user);
		List<AccumulatedItem> accumulatedItems = manageAccumulation(monthYear, "expenditure");
		this.expendituresSum = sumUpAccumulatedItems(accumulatedItems);

		return new AccumulatedItemResponse(accumulatedItems, expendituresSum, expendituresAverage);
	}
	
	@RequestMapping(value = "/get-summary-table", method = RequestMethod.POST)
	public @ResponseBody SummaryResponse getSummary() {
		return new SummaryResponse(incomesNetSum.subtract(expendituresSum), 
				                   incomesNetAverage.subtract(expendituresAverage));
	}
	
	private List<AccumulatedItem> manageAccumulation(MonthYear monthYear, String type) {
		
		List<? extends Item> items = null;
		
		if (type.equals("income")) {
			items = incomeRepository.findByMonthYear(monthYear);
		} else if (type.equals("expenditure")) {
			items = expenditureRepository.findByMonthYear(monthYear);
		}
		
		List<AccumulatedItem> accumulatedItems = generateAccumulatedItems(items, type);
		
		return accumulatedItems.stream()
				.sorted(Comparator.comparing(a -> a.getSubCategory().getCategory().getName()))
				.collect(Collectors.toList());
	}
	
	private BigDecimal sumUpAccumulatedItems(List<AccumulatedItem> accumulatedItems) {
		return accumulatedItems.stream()
				.map(it -> it.getSumValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
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
		
		return sum.divide(new BigDecimal(numOfFinishedMonths), 2, RoundingMode.HALF_UP)
				.setScale(2, RoundingMode.CEILING);
	}
	
}

// Auxiliary classes

class AccumulatedIncomesResponse {
	
	public List<AccumulatedItem> accumulatedItems;
	public BigDecimal grossSum;
	public BigDecimal grossAverage;
	public BigDecimal donationsSum;
	public BigDecimal donationsAverage;
	public BigDecimal netSum;
	public BigDecimal netAverage;
	
	public AccumulatedIncomesResponse withAccumulatedItems(List<AccumulatedItem> accumulatedItems) {
		this.accumulatedItems = accumulatedItems;
		return this;
	}
	
	public AccumulatedIncomesResponse withGrossSum(BigDecimal grossSum) {
		this.grossSum = grossSum;
		return this;
	}
	
	public AccumulatedIncomesResponse withGrossAverage(BigDecimal grossAverage) {
		this.grossAverage = grossAverage;
		return this;
	}
	
	public AccumulatedIncomesResponse withDonationsSum(BigDecimal donationsSum) {
		this.donationsSum = donationsSum;
		return this;
	}
	
	public AccumulatedIncomesResponse withDonationsAverage(BigDecimal donationsAverage) {
		this.donationsAverage = donationsAverage;
		return this;
	}
	
	public AccumulatedIncomesResponse withNetSum(BigDecimal netSum) {
		this.netSum = netSum;
		return this;
	}
	
	public AccumulatedIncomesResponse withNetAverage(BigDecimal netAverage) {
		this.netAverage = netAverage;
		return this;
	}
	
}

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
