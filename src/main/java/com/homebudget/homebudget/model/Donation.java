package com.homebudget.homebudget.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "donation")
public class Donation implements Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "date")
	private Date dateTime;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "value")
	private BigDecimal value;
	
	@OneToOne
	private MonthYear monthYear;
	
	@OneToOne
	private User user;
	
	public Donation() {
		// empty
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public MonthYear getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(MonthYear monthYear) {
		this.monthYear = monthYear;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public SubCategory getSubCategory() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
