package com.homebudget.homebudget.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "month_year")
public class MonthYear {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "month")
	private int month;
	
	@Column(name = "year")
	private int year;
	
	@Column(name = "donation_rate")
	private BigDecimal donationRate;

	@OneToOne
	private User user;
	
	public MonthYear() {
		//empty
	}
	
	public MonthYear(int month, int year, User user) {
		this.month = month;
		this.year = year;
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public BigDecimal getDonationRate() {
		return donationRate;
	}

	public void setDonationRate(BigDecimal donationRate) {
		this.donationRate = donationRate;
	}
	
}
