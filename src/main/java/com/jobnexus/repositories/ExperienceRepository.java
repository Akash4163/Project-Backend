package com.jobnexus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobnexus.entities.Experience;

public interface ExperienceRepository extends JpaRepository<Experience, Integer> {

}
