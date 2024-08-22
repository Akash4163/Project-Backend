package com.jobnexus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobnexus.entities.Job;

public interface JobRepository extends JpaRepository<Job, Integer> {

}
