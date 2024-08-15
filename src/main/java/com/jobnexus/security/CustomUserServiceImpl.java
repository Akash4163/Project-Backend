package com.jobnexus.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobnexus.entities.JobSeeker;
import com.jobnexus.entities.Recruiter;
import com.jobnexus.exception.ApiCustomException;
import com.jobnexus.repositories.JobSeekerRepository;
import com.jobnexus.repositories.RecruiterRepository;

@Service
@Transactional
public class CustomUserServiceImpl implements UserDetailsService{
	
	@Autowired
	private JobSeekerRepository jobSeekerRepository;
	@Autowired
	private RecruiterRepository recruiterRepository;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
	    JobSeeker jobseeker = null;
	    
	    if (recruiter == null) {
	        jobseeker = jobSeekerRepository.findByEmail(email).orElse(null);
	    }
	    if (jobseeker == null && recruiter == null) {
	        throw new UsernameNotFoundException("User not found with email: " + email);
	    }
	    
	    if (recruiter != null) {
	        return new CustomUser(recruiter.getEmail(), recruiter.getPassword(), "ROLE_RECRUITER");
	    }
	    
	    return new CustomUser(jobseeker.getEmail(), jobseeker.getPassword(), "ROLE_JOBSEEKER");
	}

	
	

}
