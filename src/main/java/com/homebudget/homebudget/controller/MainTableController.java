package com.homebudget.homebudget.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.service.MonthYearRepository;

@Controller
public class MainTableController {

	@Autowired
	MonthYearRepository monthYearRepository;
	
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
		
		return "main-table";
	}
	
	@RequestMapping(value = "/change-year", method = RequestMethod.POST)
	public @ResponseBody List<Integer> changeYearAndGetMonths(@RequestBody Year year) {
		
		List<MonthYear> monthsYears = monthYearRepository.findAll();
		
		return getMonthsSortedDescForGivenYear(monthsYears, Integer.parseInt(year.year));
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
