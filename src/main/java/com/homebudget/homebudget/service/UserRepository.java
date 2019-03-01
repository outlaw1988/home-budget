package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.User;


public interface UserRepository extends JpaRepository<User, Integer> {

	List<User> findByUsername(String username);
	List<User> findByEmail(String email);
	List<User> findByResetToken(String resetToken);
	
}
