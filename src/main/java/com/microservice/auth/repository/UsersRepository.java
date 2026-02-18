package com.microservice.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservice.auth.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{
	
	@Query("SELECT u FROM Users u WHERE u.userMail = :userMail")
	Users findByMailId(@Param("userMail") String userMail);
	
}
