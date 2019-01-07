package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.MonthYear;

public interface MonthYearRepository extends JpaRepository<MonthYear, Integer> {

	List<MonthYear> findByMonthAndYear(int month, int year);
	
}
