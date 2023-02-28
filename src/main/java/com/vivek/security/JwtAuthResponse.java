package com.vivek.security;

import com.vivek.dto.UserDTO;

import lombok.Data;

@Data
public class JwtAuthResponse {
	private String token;
	private UserDTO user;
}
