package com.homebudget.homebudget.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.Donation;
import com.homebudget.homebudget.model.MonthYear;

public interface DonationRepository extends JpaRepository<Donation, Integer> {

	List<Donation> findByMonthYearOrderByDateTimeDesc(MonthYear monthYear);
	
}
