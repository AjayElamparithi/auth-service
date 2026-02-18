package com.microservice.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="USERS")
@Data
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="USER_ID")
	public Long userId;
	
	@Column(name="USER_NAME")
	public String userName;
	
	@Column(name="USER_MAIL")
	public String userMail;
	
	@Column(name="LOGIN_PASSWORD")
	public String loginPassword;
	
	@Column(name="USER_ROLE")
	public String userRole;
	
}
