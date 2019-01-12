package com.homebudget.homebudget.model;

public class AccumulatedItem {
	
	private SubCategory subCategory;
	private float sumValue;
	private Type type;
	
	public AccumulatedItem(SubCategory subCategory, float sumValue, Type type) {
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

	public float getSumValue() {
		return sumValue;
	}

	public void setSumValue(float sumValue) {
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
	
	public enum Type {
		EXPENDITURE, INCOME;
	}
	
}


