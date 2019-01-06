package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Integer> {

	List<Expenditure> findAllByOrderByDateTimeDesc();
	
}
