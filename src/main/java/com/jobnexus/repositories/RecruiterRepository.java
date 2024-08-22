package com.jobnexus.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobnexus.entities.Recruiter;

public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {

	Optional<Recruiter> findByEmailAndPassword(String email, String password);
	Optional<Recruiter> findByEmail(String email);
	boolean existsRecruiterByEmail(String email);
	boolean existsRecruiterByCompanyUrl(String url);
	boolean existsRecruiterByUsername(String username);
}
