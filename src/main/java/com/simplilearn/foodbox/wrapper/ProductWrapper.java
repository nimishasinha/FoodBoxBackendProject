package com.simplilearn.foodbox.wrapper;

public class ProductWrapper {
	
	Integer id;
	
	String name;
	
	String description;
	
	Integer price;
	
	String status;
	
	Integer categoryId;
	
	String categoryName;
	
	public ProductWrapper() {
		
	}

	public ProductWrapper(Integer id,String name, String description ,Integer price ,String status, Integer categoryId ,String categoryName) 
	{
		this.id=id;
		this.name=name;
		this.description=description;
		this.price=price;
		this.status=status;
		this.categoryId=categoryId;
		
	}
	
	public ProductWrapper(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public ProductWrapper(Integer id, String name , String description , Integer price) {
		this.id = id;
		this.name = name;
		this.description=description;
		this.price=price;
	}
}
