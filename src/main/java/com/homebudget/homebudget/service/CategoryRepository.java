package com.homebudget.homebudget.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	
	
}
