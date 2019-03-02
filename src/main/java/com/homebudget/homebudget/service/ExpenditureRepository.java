package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Expenditure;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.User;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Integer> {

	List<Expenditure> findByUserOrderByDateTimeDesc(User user);
	List<Expenditure> findByMonthYear(MonthYear monthYear);
	Expenditure findById(int id);
	
}
