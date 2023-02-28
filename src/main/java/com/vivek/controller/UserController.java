package com.vivek.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vivek.dto.UserDTO;
import com.vivek.model.User;
import com.vivek.repository.UserRepo;
import com.vivek.security.JwtAuthRequest;
import com.vivek.security.JwtAuthResponse;
import com.vivek.security.JwtTokenHelper;
import com.vivek.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private JwtTokenHelper jwtTokenHelper;
	
	@Autowired
	private UserDetailsService userDetailService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping("/create")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
		UserDTO user = userService.save(userDTO);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}
	
	// GET - user get
	@GetMapping("/")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		return ResponseEntity.ok(this.userService.getAllUsers());
	}
	
	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest jwtAuthrequest) throws Exception{
		this.authenticate(jwtAuthrequest.getUsername(),jwtAuthrequest.getPassword());
		UserDetails userDetails = this.userDetailService.loadUserByUsername(jwtAuthrequest.getUsername());
		String generateToken = this.jwtTokenHelper.generateToken(userDetails);
		JwtAuthResponse res = new JwtAuthResponse();
		res.setToken(generateToken);
		res.setUser(this.modelMapper.map((User)userDetails, UserDTO.class));
		return new ResponseEntity<JwtAuthResponse>(res,HttpStatus.OK);
	}

	private void authenticate(String username, String password) throws Exception {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		try {
			this.authenticationManager.authenticate(authenticationToken);	
		} catch (BadCredentialsException e) {
			System.out.println("invalid details");
			throw new Exception("Invalid username or password");
		}
		
	}
	
//	@GetMapping("/token")
//	public void fetchingDetails(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException{
//		try {
//			String token = "";
//			String username = "";
//			String requestToken = request.getHeader("Authorization");
//			if (requestToken != null && requestToken.startsWith("Bearer")) {
//				token = requestToken.substring(7);
//			}
//			String payload = token.split("\\.")[1];
//			username= new String(Base64.decodeBase64(payload),"UTF-8");
//			System.out.println("************"+username);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}	
//	}
}
