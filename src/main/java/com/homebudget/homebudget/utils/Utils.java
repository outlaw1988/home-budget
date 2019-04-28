package com.homebudget.homebudget.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.MonthYearRepository;

public class Utils {

	public static MonthYear checkAndAddMonthYear(Date date, MonthYearRepository monthYearRepository, 
			User user) {
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		int year = localDate.getYear();
		int month = localDate.getMonthValue();
		
		List<MonthYear> monthYearList = monthYearRepository.findByMonthAndYearAndUser(month, year, user);
		
		if (monthYearList.size() == 0) return monthYearRepository.save(new MonthYear(month, year, user));
		else return monthYearList.get(0);
	}
	
	public static List<Integer> getYearsSortedDesc(List<MonthYear> monthsYears) {
		
		Set<Integer> years = new HashSet<>();
		
		for (MonthYear monthYear : monthsYears) {
			years.add(monthYear.getYear());
		}
		
		List<Integer> yearsSorted = new ArrayList<Integer>(years);
		Collections.sort(yearsSorted, Collections.reverseOrder());
		
		return yearsSorted;
	}
	
	public static List<Integer> getMonthsSortedDescForGivenYear(List<MonthYear> monthsYears, Integer year) {
		
		List<Integer> months = new ArrayList<Integer>();
		
		for (MonthYear monthYear : monthsYears) {
			if (monthYear.getYear() == year) {
				months.add(monthYear.getMonth());
			}
		}
		
		Collections.sort(months, Collections.reverseOrder());
		
		return months;
	}
	
	public static String getLoggedInUserName() {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		if (principal instanceof UserDetails)
			return ((UserDetails) principal).getUsername();

		return principal.toString();
	}
	
	public static Date getCurrentWarsawTime() {
		LocalDateTime ldt = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("Europe/Warsaw"));
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}
	
}
