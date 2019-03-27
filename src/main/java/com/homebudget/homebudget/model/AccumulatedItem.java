package com.homebudget.homebudget.model;

import java.math.BigDecimal;
import com.homebudget.homebudget.utils.Type;

public class AccumulatedItem {
	
	private SubCategory subCategory;
	private BigDecimal sumValue;
	private Type type;
	
	public AccumulatedItem(SubCategory subCategory, BigDecimal sumValue, Type type) {
		this.subCategory = subCategory;
		this.sumValue = sumValue;
		this.type = type;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public BigDecimal getSumValue() {
		return sumValue;
	}

	public void setSumValue(BigDecimal sumValue) {
		this.sumValue = sumValue;
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "AccumulatedIncome [subCategory=" + subCategory + ", sumValue=" + sumValue + "]";
	}
	
}


