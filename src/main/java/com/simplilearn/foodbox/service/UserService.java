package com.simplilearn.foodbox.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.simplilearn.foodbox.wrapper.UserWrapper;

public interface UserService {
	
	ResponseEntity<String> signUp(Map<String,String> responseMap);
	
	ResponseEntity<String> login(Map<String,String> responseMap);
	
	ResponseEntity<List<UserWrapper>> getAllUser();
	
	ResponseEntity<String> update(Map<String,String> requestMap);
	
	ResponseEntity<String> checkToken();
	
	ResponseEntity<String> changePassword(Map <String , String> requestMap);
	
	ResponseEntity<String> forgotPassword(Map<String ,String> requestMap);

}
