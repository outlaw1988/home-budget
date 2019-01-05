package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findByType(String type);
	Category findById(int id);
	
}
