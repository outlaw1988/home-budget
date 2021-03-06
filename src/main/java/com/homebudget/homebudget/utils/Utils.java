package com.homebudget.homebudget.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.homebudget.homebudget.model.Item;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.service.MonthYearRepository;

public class Utils {

	public static MonthYear checkAndAddMonthYear(Date date, MonthYearRepository monthYearRepository, 
			User user) {
		
		LocalDateTime localDate = date.toInstant().atZone(ZoneId.of("Europe/Warsaw")).toLocalDateTime();
		
		int year = localDate.getYear();
		int month = localDate.getMonthValue();
		
		MonthYear monthYear = monthYearRepository.findByMonthAndYearAndUser(month, year, user);
		
		if (monthYear == null) return monthYearRepository.save(new MonthYear(month, year, user));
		else return monthYear;
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
	
	public static int getNumberOfFinishedMonths(List<MonthYear> monthsYears) {
		
        LocalDateTime localDate = getCurrentWarsawTime().toInstant().atZone(ZoneId.of("Europe/Warsaw"))
        						  .toLocalDateTime();
		
		int currYear = localDate.getYear();
		int currMonth = localDate.getMonthValue();
		int counter = 0;
		
		for (MonthYear my : monthsYears) {
			if ((my.getYear() < currYear) || 
				((my.getYear() == currYear) && (my.getMonth() < currMonth))) counter++;
		}
		
		return counter;
	}
	
	public static MonthYear getPreviousMonthYear(MonthYearRepository repository, 
												 MonthYear currMonthYear, User user) {
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("dd-MM-yyyy")
					.parse("10-" + currMonthYear.getMonth() + "-" + currMonthYear.getYear());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		
		MonthYear prevMonthYear = repository.findByMonthAndYearAndUser(
							calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), user);
		
		if (prevMonthYear == null) return null;
		else return prevMonthYear;
	}
	
	public static BigDecimal sumUpItems(List<? extends Item> items) {
		return items.stream()
				.map(d -> d.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
}
