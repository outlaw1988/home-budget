package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.User;

public interface MonthYearRepository extends JpaRepository<MonthYear, Integer> {

	MonthYear findByMonthAndYearAndUser(int month, int year, User user);
	List<MonthYear> findByUser(User user);
	
}
