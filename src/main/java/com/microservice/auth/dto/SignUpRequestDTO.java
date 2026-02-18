package com.microservice.auth.dto;

import lombok.Data;

@Data
public class SignUpRequestDTO {
	public String userName;
	public String userMail;
	public String userRole;
	public String loginPassword;
}
