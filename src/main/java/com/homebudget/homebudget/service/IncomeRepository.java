package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Income;

public interface IncomeRepository extends JpaRepository<Income, Integer> {
	
	List<Income> findAllByOrderByDateTimeDesc();
	
}
