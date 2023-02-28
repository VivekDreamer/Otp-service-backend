package com.vivek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vivek.model.Otp;

@Repository
public interface OtpRepo extends JpaRepository<Otp, Long>{

}
