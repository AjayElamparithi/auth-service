package com.microservice.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.microservice.auth.entity.Users;
import com.microservice.auth.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService{

	private final UsersRepository usersRepository;

	private boolean isValidEmail(String email) {
		return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	}

	@Override
	public UserDetails loadUserByUsername(String usermail) throws UsernameNotFoundException {

		if(usermail == null || !isValidEmail(usermail)) {
			throw new UsernameNotFoundException(usermail+ " not valid");
		}
		
		
		Users userDetails = usersRepository.findByMailId(usermail);
		
		if(userDetails == null) {
			throw new UsernameNotFoundException(usermail);
		}
		
		 return org.springframework.security.core.userdetails.User
			        .withUsername(userDetails.getUserMail())
			        .password(userDetails.getLoginPassword())
			        .roles(userDetails.getUserRole()) 
			        .accountExpired(false)
			        .accountLocked(false)
			        .credentialsExpired(false)
			        .build();
	}
	
	
	
}
