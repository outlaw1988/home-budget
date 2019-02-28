package com.homebudget.homebudget.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.service.MonthYearRepository;

public class Utils {

	public static MonthYear checkAndAddMonthYear(Date date, MonthYearRepository monthYearRepository) {
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		int year = localDate.getYear();
		int month = localDate.getMonthValue();
		
		List<MonthYear> monthYearList = monthYearRepository.findByMonthAndYear(month, year);
		
		if (monthYearList.size() == 0) return monthYearRepository.save(new MonthYear(month, year));
		else return monthYearList.get(0);
	}
	
	public static String getLoggedInUserName() {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		if (principal instanceof UserDetails)
			return ((UserDetails) principal).getUsername();

		return principal.toString();
	}
	
}
