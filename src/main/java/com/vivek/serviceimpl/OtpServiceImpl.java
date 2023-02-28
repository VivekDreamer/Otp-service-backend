package com.vivek.serviceimpl;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.vivek.model.Otp;
import com.vivek.model.User;
import com.vivek.repository.OtpRepo;
import com.vivek.repository.UserRepo;
import com.vivek.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

	@Autowired
	private OtpRepo otpRepo;
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public String sendOTP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String emailFromToken = getEmailFromToken(request, response);
		//first fetch user from the email and check is there otp already exist or not 
		//A user can request for otp after 1 min. only
		User user = userRepo.findByEmail(emailFromToken).get();
		Otp otp = user.getOtp();
		
		int requestOtp = requestOtp();
		if(otp == null) {
			boolean sendEmail = sendEmail("OTP verification", "Your otp is : " + requestOtp, emailFromToken);
			if(sendEmail) {
				saveOTP(requestOtp, user);
				return "otp has been sent in your email";
			}
			else
				return "Please try again";
		}
		LocalDateTime createdAt = user.getOtp().getCreatedAt();
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(createdAt, now);
		if(otp != null && duration.getSeconds() < 60) {
			return "Otp is sent on your mail, please try after one minute!!";
		}
		else {
			System.out.println(emailFromToken + "----------------------------------");
			boolean sendEmail = sendEmail("OTP verification", "Your otp is : " + requestOtp, emailFromToken);
			if (sendEmail) {
				Long id = user.getOtp().getId();
				Otp otpAlreadySaved = otpRepo.findById(id).get();
				updateOTP(otpAlreadySaved, requestOtp);
				return "otp has been sent in your email";
			} else {
				return "Please try again";
			}
		}
	}
	
	@Override
	public boolean validateOtp(HttpServletRequest request, HttpServletResponse response, String otpFromQuery) throws ServletException, IOException{
		System.out.println("otpFromQuery ----> "+otpFromQuery);
		String emailFromToken = getEmailFromToken(request, response);
		User user = userRepo.findByEmail(emailFromToken).get();
		
		String otpInDataBase =  user.getOtp().getOtp();
		if(otpInDataBase.equals(otpFromQuery))
			return true;
		else
			return false;
	}
	
	public void saveOTP(int requestOtp, User user) {
		Otp otpToBeSaved = Otp.builder().otp(String.valueOf(requestOtp)).createdAt(LocalDateTime.now()).build();
		otpRepo.save(otpToBeSaved);
		User updatedUser = User.builder().email(user.getEmail()).id(user.getId()).name(user.getName())
				.password(user.getPassword()).otp(otpToBeSaved).build();
		userRepo.save(updatedUser);
	}
	
	public void updateOTP(Otp otp, int requestOtp) {
		otp.setOtp(String.valueOf(requestOtp));
		otp.setCreatedAt(LocalDateTime.now());
		otpRepo.save(otp);
	}
	
	public boolean sendEmail(String subject, String message,String to)
	{
		boolean isMessageSent = false;
		String from = "";
		//use your password
		String pass = "";
		
		//variable to store gmail host
		String host = "smtp.gmail.com";
		//getting system properties
		Properties properties = System.getProperties();
		System.out.println("properties : "+properties);
		//setting imp info. in properties object
		//setting host 
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable","true");
		properties.put("mail.smtp.auth", "true");
		
		//getting session object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, pass);
			}
		});
		
		session.setDebug(true);
		//compose the message(text,multimedia etc)
		
		MimeMessage messageToBeSent = new MimeMessage(session);
		try {
			messageToBeSent.setFrom(from);
			messageToBeSent.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			messageToBeSent.setSubject(subject);
			messageToBeSent.setContent("<h1>"+message+"</h1>", "text/html");
			
			//send the message by using Transport class
			Transport.send(messageToBeSent);
			isMessageSent = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isMessageSent;
	}
	
	public String getEmailFromToken(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String username = "";
		try {
			String token = "";
			String requestToken = request.getHeader("Authorization");
			if (requestToken != null && requestToken.startsWith("Bearer")) {
				token = requestToken.substring(7);
			}
			String payload = token.split("\\.")[1];
			username= new String(Base64.decodeBase64(payload),"UTF-8");
			Pattern pattern = Pattern.compile("\"sub\":\"(.*?)\"");
	        Matcher matcher = pattern.matcher(username);
	        if (matcher.find()) {
	            username = matcher.group(1);
	        }
			//System.out.println("************"+username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return username;
	}
	
	@Override
	public int requestOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return otp;
	}
}
