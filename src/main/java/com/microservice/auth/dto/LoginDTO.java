package com.microservice.auth.dto;

import lombok.Data;

@Data
public class LoginDTO {
	public String userMail;
	public String loginPassword;
}
