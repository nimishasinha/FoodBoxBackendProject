package com.simplilearn.foodbox.wrapper;


public class UserWrapper {
	
	UserWrapper userWrapper = new UserWrapper(1,"neha","neha@n.com","1234","false");
	
	private Integer id;
	
	private String name;
	
	private String email;
	
	private String contactNumber ;
	
	private String status;



	public UserWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserWrapper(Integer id, String name, String email, String contactNumber, String status) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.contactNumber = contactNumber;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
