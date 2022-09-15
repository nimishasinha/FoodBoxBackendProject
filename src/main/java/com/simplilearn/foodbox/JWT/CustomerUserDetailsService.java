package com.simplilearn.foodbox.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;

import com.simplilearn.foodbox.dao.UserDao;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
	

	@Autowired
	UserDao userDao;
	
	private com.simplilearn.foodbox.pojo.User userDetails;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		  userDetails = userDao.findByEmailId(username);
		  if(!Objects.isNull(userDetails)) {
			  return new User(userDetails.getEmail(),userDetails.getPassword(), new ArrayList<>());
		  }
		  else {
			  throw new UsernameNotFoundException("User not found");
		  }
	}
	
	public com.simplilearn.foodbox.pojo.User getUserDetails(){
		
		com.simplilearn.foodbox.pojo.User user = userDetails;
		user.setPassword(null);
		return userDetails;
	}
	
	
	
	
	
}
