package com.homebudget.homebudget.model;

import java.math.BigDecimal;

public interface Item {

	int getId();
	SubCategory getSubCategory();
	BigDecimal getValue();
	MonthYear getMonthYear();
	
}
