package com.simplilearn.foodbox.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simplilearn.foodbox.pojo.Category;

public interface CategoryDao extends JpaRepository<Category, Integer>{
	
	List<Category> getAllCategory();

}
