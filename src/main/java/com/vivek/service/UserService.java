package com.vivek.service;

import java.util.List;

import com.vivek.dto.UserDTO;
import com.vivek.model.User;

public interface UserService {
	UserDTO save(UserDTO userDTO);
	List<UserDTO> getAllUsers();
	UserDTO getUserById(Long userId);
}
