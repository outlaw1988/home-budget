package com.homebudget.homebudget.model;

public class AccumulatedIncome {
	
	private SubCategory subCategory;
	private float sumValue;
	
	public AccumulatedIncome(SubCategory subCategory, float sumValue) {
		this.subCategory = subCategory;
		this.sumValue = sumValue;
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
	
//	@Override
//	public String toString() {
//		return "AccumulatedIncome [subCategory=" + subCategory + ", sumValue=" + sumValue + "]";
//	}
	
}
