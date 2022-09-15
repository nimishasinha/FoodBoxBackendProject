package com.simplilearn.foodbox.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.simplilearn.foodbox.JWT.CustomerUserDetailsService;
import com.simplilearn.foodbox.JWT.JwtFilter;
import com.simplilearn.foodbox.JWT.JwtUtils;
import com.simplilearn.foodbox.constants.FoodboxConstants;
import com.simplilearn.foodbox.dao.UserDao;
import com.simplilearn.foodbox.pojo.User;
import com.simplilearn.foodbox.service.UserService;
import com.simplilearn.foodbox.utils.EmailUtils;
import com.simplilearn.foodbox.utils.FoodBoxUtils;
import com.simplilearn.foodbox.wrapper.UserWrapper;


@Service
public class UserServiceImpl implements UserService {
	
	
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	JwtFilter jwtFilter; 
	
	@Autowired
	EmailUtils emailUtils;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		try {
		 
		if(validateSignupMap(requestMap)) {
			
			User user = userDao.findByEmailId(requestMap.get("email"));
			if(Objects.isNull(user)) {
				userDao.save(getUserDataFromMap(requestMap));
				return FoodBoxUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
			}else {
				return FoodBoxUtils.getResponseEntity("Email ALredy Registered", HttpStatus.BAD_REQUEST);
			}
			
			
		}
		else
		{
			return FoodBoxUtils.getResponseEntity(FoodboxConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}
		
	}catch(Exception ex) {
	   ex.printStackTrace();
	}
	return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private boolean validateSignupMap(Map<String , String> requestMap)
	{
		if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password"))
			
			{
			return true;
			
			}
		else 
		{
			return false;
		}
			
		
	}
	
	private User getUserDataFromMap(Map<String,String> requestMap) {
		
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		
		return user;
		
		
		
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password")));
			
			if(auth.isAuthenticated()) {
				
				if(customerUserDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true"))
				{
					return new ResponseEntity<String>("{\"token\":\""+
				jwtUtils.generateToken(customerUserDetailsService.getUserDetails().getEmail(),
						customerUserDetailsService.getUserDetails().getRole()) +"\"}",
							HttpStatus.OK);
				}
				else {
					return new ResponseEntity<String>("{\"message\":\""+"wait for admin approval."+"\"}",HttpStatus.BAD_REQUEST);
				}
				
			}
		}
		catch(Exception ex) {
			
		}
		return new ResponseEntity<String>("{\"message\":\""+"Bad Credentials"+"\"}",HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
				
			}else {
				return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
			Optional<User> optional =	userDao.findById(Integer.parseInt(requestMap.get("id")));
			 if(!optional.isEmpty())
			 {
				 userDao.updateStatus(requestMap.get("status") ,Integer.parseInt( requestMap.get("id")));
				 sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
				 return FoodBoxUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
			 }
			}else {
				return FoodBoxUtils.getResponseEntity(FoodboxConstants.UNAUTHOSRIZED_ACCESS, HttpStatus.UNAUTHORIZED); 
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR); 
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status != null && status.equalsIgnoreCase("true"))
        {
        	emailUtils.simpleSendMessage(jwtFilter.getCurrentUser(), "Account Approved", "User :-" + user + "\n is approved by \n Admin:-" + jwtFilter.getCurrentUser(), allAdmin);
        }else
        {
        	emailUtils.simpleSendMessage(jwtFilter.getCurrentUser(), "Account Disabled", "User :-" + user + "\n is disabled by \n Admin:-" + jwtFilter.getCurrentUser(), allAdmin);
        }
	}

	@Override
	public ResponseEntity<String> checkToken() {
		return FoodBoxUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			
			User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
			if(userObj.equals(null)) {
				if(userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return FoodBoxUtils.getResponseEntity("Passowrd updated successfully ", HttpStatus.OK);
				}
				
			}else {
				return FoodBoxUtils.getResponseEntity("Incorrect Password", HttpStatus.BAD_REQUEST); 
			}
			return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR); 
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR); 
	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		
		try {
			User user = userDao.findByEmail(requestMap.get("email"));
			if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
				emailUtils.forgotMail(user.getEmail(), "Credentials from FoodBox management", user.getPassword());
				
				return FoodBoxUtils.getResponseEntity("Check your mail for credentials", HttpStatus.OK);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return FoodBoxUtils.getResponseEntity(FoodboxConstants.SOMETHING_WENT_WORNG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
			

}
