package com.simplilearn.foodbox.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "User.findByEmailId" , query="select u from User u where u.email=:email")

@NamedQuery(name="User.getAllUser" , query="select new com.simplilearn.foodbox.wrapper.UserWrapper(u.id,u.name,u.contactNumber,u.email,u.status) from User u where u.role='user'")

@NamedQuery(name="User.updateStatus" , query="update User u set u.status = :status where u.id =:id")

@NamedQuery(name="User.getAllAdmin" , query="select u.email from User u where u.role='admin'")

@Entity
@Table(name="user")
@DynamicUpdate
@DynamicInsert
public class User {
	
	private static final long serilaVersionUID =1L;
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="name")
	private String name ;
	
	@Column(name="contactNumber")
	private String contactNumber;
	
	@Column(name="email")
	private String email ;
	
	@Column(name="password")
	private String password ;
	
	@Column(name="status")
	private String status;
	
	@Column(name="role")
	private String role;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String name, String contactNumber, String email, String password, String status, String role) {
		super();
		this.name = name;
		this.contactNumber = contactNumber;
		this.email = email;
		this.password = password;
		this.status = status;
		this.role = role;
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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public static long getSerilaversionuid() {
		return serilaVersionUID;
	}
	
	
	
	

}
