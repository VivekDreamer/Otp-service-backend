package com.vivek.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;

import com.vivek.model.Otp;

public interface OtpService {
	String sendOTP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	int requestOtp();
	boolean validateOtp(HttpServletRequest request, HttpServletResponse response, String otpFromQuery) throws ServletException, IOException;
}
