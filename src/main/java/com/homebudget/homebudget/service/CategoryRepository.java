package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Category;
import com.homebudget.homebudget.model.User;
import com.homebudget.homebudget.utils.Type;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findByTypeAndUserOrderByName(Type type, User user);
	List<Category> findByUser(User user);
	List<Category> findByUserOrderByName(User user);
	List<Category> findByUserAndNameAndType(User user, String name, Type type);
	Category findById(int id);
	
}
