package com.simplilearn.foodbox.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.simplilearn.foodbox.JWT.JwtFilter;
import com.simplilearn.foodbox.constants.FoodboxConstants;
import com.simplilearn.foodbox.dao.CategoryDao;
import com.simplilearn.foodbox.pojo.Category;
import com.simplilearn.foodbox.service.CategoryService;
import com.simplilearn.foodbox.utils.FoodBoxUtils;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(ValidateCategoryMap(requestMap,false)) {
					categoryDao.save(getCategoryFromMap(requestMap,false));
					return FoodBoxUtils.getResponseEntity("Category Dded successfully",HttpStatus.OK);
				}
				
			}else {
				return FoodBoxUtils.getResponseEntity(FoodboxConstants.UNAUTHOSRIZED_ACCESS, HttpStatus.BAD_REQUEST);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private boolean ValidateCategoryMap(Map<String,String> requestMap, boolean validateId)
	{
		if(requestMap.containsKey("name")) {
			if(requestMap.containsKey("id") && validateId) {
				return true;
			}else if (!validateId){
				return true;
			}
		}
		return false;
	}
	
	private Category getCategoryFromMap(Map<String,String > requestMap , Boolean isAdd)
	{
		Category category = new Category();
		if(isAdd) {
			category.setId(Integer.parseInt(requestMap.get("id")));
		}
		category.setName(requestMap.get("name"));
		return category;
		
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			if(!Strings.isNullOrEmpty(filterValue)  && filterValue.equalsIgnoreCase("true"))
			{
				return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
			}
			return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(ValidateCategoryMap(requestMap, true))
				{
					Optional optional =categoryDao.findById(Integer.parseInt(requestMap.get("id")));
					if(!optional.isEmpty()) {
						categoryDao.save(getCategoryFromMap(requestMap,true));
						return FoodBoxUtils.getResponseEntity("Category successfully updated", HttpStatus.OK);
					}else {
						return FoodBoxUtils.getResponseEntity("Category Id does not exist",HttpStatus.BAD_REQUEST);
					}
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
