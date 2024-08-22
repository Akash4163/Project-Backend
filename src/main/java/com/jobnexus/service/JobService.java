package com.jobnexus.service;

import java.util.List;

import com.jobnexus.requestDTO.JobRequestDTO;
import com.jobnexus.responseDTO.ApplicantResponseDTO;
import com.jobnexus.responseDTO.JobListResponseDTO;
import com.jobnexus.responseDTO.JobResponseDTO;

public interface JobService {
	List<JobListResponseDTO> getAllJobs();
	String createJob(JobRequestDTO jobRequestDTO);
	String deleteJob(String email,Integer id);
	JobResponseDTO getJobDetails(Integer id);
	List<ApplicantResponseDTO> getApplicantsOfJob(String email, Integer id);

}
