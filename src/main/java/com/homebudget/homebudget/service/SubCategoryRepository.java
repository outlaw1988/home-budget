package com.homebudget.homebudget.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homebudget.homebudget.model.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {

}
