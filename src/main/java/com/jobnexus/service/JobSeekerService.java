package com.jobnexus.service;

import org.springframework.web.multipart.MultipartFile;

import com.jobnexus.entities.JobSeeker;
import com.jobnexus.requestDTO.jobSeekerDTO.JobSeekerCredsRequestDTO;
import com.jobnexus.requestDTO.jobSeekerDTO.JobSeekerRequestDTO;
import com.jobnexus.responseDTO.JobSeekerResponseDTO;

import io.jsonwebtoken.io.IOException;

public interface JobSeekerService {

	String registerJobSeeker(JobSeekerRequestDTO jobSeekerRequestDTO);
	JobSeekerResponseDTO getJobseeker(String email);
	void deleteJobSeeker(String email);
	String saveFiles(Integer id,MultipartFile image, MultipartFile resume) throws IOException;
	byte[] getResume(String jobSeekerEmail);
	boolean checkEmail(String email);

}
