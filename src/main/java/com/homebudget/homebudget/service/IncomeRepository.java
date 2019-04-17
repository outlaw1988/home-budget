package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Expenditure;
import com.homebudget.homebudget.model.Income;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.SubCategory;
import com.homebudget.homebudget.model.User;

public interface IncomeRepository extends JpaRepository<Income, Integer> {
	
	List<Income> findByUserOrderByDateTimeDesc(User user);
	List<Income> findByMonthYear(MonthYear monthYear);
	List<Income> findByMonthYearOrderByDateTimeDesc(MonthYear monthYear);
	List<Income> findBySubCategory(SubCategory subCategory);
	Income findById(int id);
	
}
