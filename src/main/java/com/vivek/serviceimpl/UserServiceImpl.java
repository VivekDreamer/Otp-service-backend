package com.vivek.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vivek.dto.UserDTO;
import com.vivek.model.User;
import com.vivek.repository.UserRepo;
import com.vivek.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Override
	public UserDTO save(UserDTO userDTO) {
		String encodedPassword = this.passwordEncoder.encode(userDTO.getPassword());
		System.out.println("**********************************************");
		System.out.println(encodedPassword);
		System.out.println("**********************************************");
		User user = User.builder()
		.name(userDTO.getName())
		.email(userDTO.getEmail())
		.password(encodedPassword)
		.otp(userDTO.getOtp())
		.build();
		
		User savedUser = userRepo.save(user);
		return userToDto(savedUser);
	}
	
	@Override
	public UserDTO getUserById(Long userId) {
		User user = this.userRepo.findById(userId).get();
		return userToDto(user);
	}
	
	@Override
	public List<UserDTO> getAllUsers() {
		List<User> users = this.userRepo.findAll();
		List<UserDTO> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());
		return userDtos;
	}
	public UserDTO userToDto(User user) {
		UserDTO userDto = this.modelMapper.map(user, UserDTO.class);
		return userDto;
	}
	public User dtoToUser(UserDTO userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		return user;
	}

	

}
