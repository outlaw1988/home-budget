package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {

	List<SubCategory> findByCategoryOrderByName(Category category);
	
}
