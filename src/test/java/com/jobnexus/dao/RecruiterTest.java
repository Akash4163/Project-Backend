package com.jobnexus.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.byteThat;

import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.jobnexus.entities.Recruiter;
import com.jobnexus.repositories.RecruiterRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RecruiterTest {
	
	@Autowired
	private RecruiterRepository recruiterRepository;

//	Passed
//	To add recruiter in the database
//	@Test
	void addRecruiter() {
		byte[] ab = {1,2};
		Recruiter recruiter = new Recruiter("Akash", "Raj", "Anpat", "AkashAnpat", "akashanpat1999@gmail.com", "Akashh@123", "Amazon", "8600825622", "Pune", "Akash.com", "1234", ab, "Company Desc");
		Recruiter recruiter2 = recruiterRepository.save(recruiter);
		System.out.println(recruiter.getFirstName()+" "+recruiter2.getFirstName());
		assertEquals(recruiter.getFirstName(), recruiter2.getFirstName());
	}
	
//	Passed
//	To get the recruiter from the database
//	@Test
	void getRecruiter() {
		Recruiter recruiter = recruiterRepository.findById(4).orElseThrow();
		assertEquals("Akash", recruiter.getFirstName());
	}
	
//	Passed 
//	To delete recruiter from the database
//	@Test
	void deleteRecruiter() {
		Recruiter recruiter = recruiterRepository.findById(4).orElseThrow();
		recruiterRepository.delete(recruiter);
		assertEquals("", "");
	}

}













