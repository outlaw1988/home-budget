package com.homebudget.homebudget.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Integer> {

	
	
}
