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
@Table(name = "income")
public class Income implements Item {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "value")
	private BigDecimal value;
	
	@OneToOne
	private SubCategory subCategory;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "date")
	private Date dateTime;
	
	@OneToOne
	private MonthYear monthYear;
	
	@OneToOne
	private User user;

	public Income() {
		//empty
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
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
	public String toString() {
		return "Income [id=" + id + ", value=" + value + ", subCategory=" + subCategory + ", description=" + description
				+ ", dateTime=" + dateTime + ", monthYear=" + monthYear + "]";
	}
	
}
