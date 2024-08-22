package com.jobnexus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobnexus.entities.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {

}
