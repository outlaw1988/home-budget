package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.User;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findByTypeAndUser(String type, User user);
	List<Category> findByUser(User user);
	Category findById(int id);
	
}
