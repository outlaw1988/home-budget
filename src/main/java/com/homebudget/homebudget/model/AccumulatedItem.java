package com.homebudget.homebudget.model;

import java.math.BigDecimal;
import com.homebudget.homebudget.utils.Type;

public class AccumulatedItem {
	
	private SubCategory subCategory;
	private BigDecimal sumValue;
	private Type type;
	private BigDecimal average;
	
	public BigDecimal getAverage() {
		return average;
	}

	public void setAverage(BigDecimal average) {
		this.average = average;
	}

	public AccumulatedItem(SubCategory subCategory, BigDecimal sumValue, Type type, BigDecimal average) {
		this.subCategory = subCategory;
		this.sumValue = sumValue;
		this.type = type;
		this.average = average;
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
		return "AccumulatedItem [subCategory=" + subCategory + ", sumValue=" + sumValue + ", type=" + type
				+ ", average=" + average + "]";
	}

}


