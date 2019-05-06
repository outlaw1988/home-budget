package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Donation;
import com.homebudget.homebudget.model.MonthYear;
import com.homebudget.homebudget.model.User;

public interface DonationRepository extends JpaRepository<Donation, Integer> {

	List<Donation> findByMonthYearOrderByDateTimeDesc(MonthYear monthYear);
	List<Donation> findByUser(User user);
	
}
