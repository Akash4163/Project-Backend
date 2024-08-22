package com.jobnexus.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.jobnexus.entities.Recruiter;
import com.jobnexus.requestDTO.RecruiterRequestDTO;
import com.jobnexus.responseDTO.RecruiterResponseDTO;

public interface RecruiterService {
	String addRecruiter(RecruiterRequestDTO recruiterRequestDTO);
	String uploadImage(int id, MultipartFile companyLogo);
	RecruiterResponseDTO getRecruiter(String recruiterEmail);
	void deleteRecruiter(String email);
	boolean checkEmail(String email);
}
