package com.vivek.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vivek.model.Otp;
import com.vivek.model.User;
import com.vivek.model.User.UserBuilder;
import com.vivek.service.OtpService;

@RestController
@RequestMapping("/user/otp/")
public class OtpController {

	@Autowired
	private OtpService otpService;
	
	@GetMapping("/sendOtp")
	public ResponseEntity<?> sendOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String saveOTP = this.otpService.sendOTP(request, response);
		if(saveOTP.equals("Otp is sent on your mail, please try after one minute!!")) {
			return new ResponseEntity<String>("Otp is sent on your mail, please try after one minute!!",HttpStatus.OK);
		}
		else if(saveOTP.equals("otp has been sent in your email")) {
			return new ResponseEntity<String>("otp has been sent in your email", HttpStatus.OK);
		}
		else
			return new ResponseEntity<String>("Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/validateOtp")
	public ResponseEntity<?> validateOtp(HttpServletRequest request, HttpServletResponse response, @RequestParam("otp") String otpFromQuery) throws ServletException, IOException{
		boolean validateOtp = this.otpService.validateOtp(request, response, otpFromQuery);
		if(validateOtp)
			return new ResponseEntity<String>("otp entered successfully. ",HttpStatus.OK);
		else
			return new ResponseEntity<String>("You entered wrong otp!!! ",HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
