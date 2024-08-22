package com.jobnexus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jobnexus.entities.Address;
import com.jobnexus.entities.Experience;
import com.jobnexus.entities.GraduationEducation;
import com.jobnexus.entities.HscEducation;
import com.jobnexus.entities.JobApplication;
import com.jobnexus.entities.JobSeeker;
import com.jobnexus.entities.SscEducation;
import com.jobnexus.exception.ApiCustomException;
import com.jobnexus.repositories.AddressRepository;
import com.jobnexus.repositories.ExperienceRepository;
import com.jobnexus.repositories.GraduationEducationRepository;
import com.jobnexus.repositories.HscEducationRepository;
import com.jobnexus.repositories.JobSeekerRepository;
import com.jobnexus.repositories.SscEducationRepository;
import com.jobnexus.requestDTO.jobSeekerDTO.ExperienceDTO;
import com.jobnexus.requestDTO.jobSeekerDTO.JobSeekerRequestDTO;
import com.jobnexus.responseDTO.AppliedJobResponseDTO;
import com.jobnexus.responseDTO.JobSeekerResponseDTO;

@Service
@Transactional
public class JobSeekerServiceImpl implements JobSeekerService {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private JobSeekerRepository jobSeekerRepository;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private PasswordEncoder encoder;

	public JobSeekerServiceImpl() {
		System.out.println("Job Seeker Service Up and Running!");
	}

	@Override
	public String registerJobSeeker(JobSeekerRequestDTO jobSeekerRequestDTO) {
		// Mapping all the Request Data to Valid Entities
		JobSeeker jobSeeker = mapper.map(jobSeekerRequestDTO.getPersonal(), JobSeeker.class);
		jobSeeker.setPassword(passwordEncoder.encode(jobSeekerRequestDTO.getPersonal().getPassword()));
		Address address = mapper.map(jobSeekerRequestDTO.getAddress(), Address.class);
		SscEducation sscEducation = mapper.map(jobSeekerRequestDTO.getEducation().getSsc(), SscEducation.class);
		HscEducation hscEducation = mapper.map(jobSeekerRequestDTO.getEducation().getHsc(), HscEducation.class);
		GraduationEducation graduationEducation = mapper.map(jobSeekerRequestDTO.getEducation().getGraduation(),
				GraduationEducation.class);
		// Fetching the List of ExperienceDTO
		List<ExperienceDTO> experienceDTO = jobSeekerRequestDTO.getExperiences();
		List<Experience> experiences = new ArrayList<>();
		// Assigning each item of ExperienceDTO to valid Experience Entity
		for (ExperienceDTO exp : experienceDTO) {
			experiences.add(mapper.map(exp, Experience.class));
		}

		if (jobSeekerRepository.existsJobSeekerByEmail(jobSeeker.getEmail()))
			throw new ApiCustomException("Email is Already Registered!");
		if (jobSeekerRepository.existsJobSeekerByUsername(jobSeeker.getUsername()))
			throw new ApiCustomException("Username is Already Registered!");

		// Setting 2-way Data for automatic ID mapping
		address.setJobSeeker(jobSeeker);
		jobSeeker.setAddress(address);

		sscEducation.setJobSeeker(jobSeeker);
		jobSeeker.setSscEducation(sscEducation);

		hscEducation.setJobSeeker(jobSeeker);
		jobSeeker.setHscEducation(hscEducation);

		graduationEducation.setJobSeeker(jobSeeker);
		jobSeeker.setGraduationEducation(graduationEducation);

		// Setting Experience One by One
		for (Experience experience : experiences) {
			jobSeeker.setExperience(experience);
		}

		// Persisting the JobSeeker Transient Entity, which will automatically Cascade
		// the Insert of
		// other Entities
		JobSeeker persistedJobSeeker = jobSeekerRepository.save(jobSeeker);
		return persistedJobSeeker.getId().toString();
	}

	@Override
	public String saveFiles(Integer id, MultipartFile image, MultipartFile resume) {
		JobSeeker jobSeeker = jobSeekerRepository.findById(id)
				.orElseThrow(() -> new ApiCustomException("User Not Found"));
		try {
			jobSeeker.setProfilePhoto(image.getBytes());
			jobSeeker.setResume(resume.getBytes());
		} catch (IOException e) {
			jobSeekerRepository.delete(jobSeeker);
			throw new ApiCustomException("File Format Not Supported");
		}
		return "Uploaded Data";
	}

	@Override
	public JobSeekerResponseDTO getJobseeker(String email) {
		JobSeeker jobSeeker = jobSeekerRepository.findByEmail(email)
				.orElseThrow(() -> new ApiCustomException("User Not Found!"));
		List<JobApplication> jobApplications = jobSeeker.getJobApplications();
		List<AppliedJobResponseDTO> appliedJobResponseDTOs = new ArrayList<AppliedJobResponseDTO>();
		for (JobApplication jobApplication : jobApplications) {

			AppliedJobResponseDTO appliedJob = new AppliedJobResponseDTO();

			appliedJob.setApplicationId(jobApplication.getId());
			appliedJob.setJobId(jobApplication.getJob().getId());
			appliedJob.setJobCategory(jobApplication.getJob().getCategory().getName());
			appliedJob.setRecruiterName(jobApplication.getJob().getRecruiter().getFirstName());
			appliedJob.setRole(jobApplication.getJob().getRole());
			appliedJob.setExpectedSalary(jobApplication.getJob().getExpectedSalary());
			appliedJob.setApplicationStatus(jobApplication.getStatus());
			appliedJob.setCompanyName(jobApplication.getJob().getRecruiter().getCompanyName());

			if (jobApplication.getJob().getRecruiter().getCompanyLogo() != null) {
				String base64CompanyLogo = Base64.getEncoder()
						.encodeToString(jobApplication.getJob().getRecruiter().getCompanyLogo());
				appliedJob.setCompanyLogo(base64CompanyLogo);
			}

			appliedJobResponseDTOs.add(appliedJob);
		}
		JobSeekerResponseDTO jobSeekerResponseDTO = mapper.map(jobSeeker, JobSeekerResponseDTO.class);
		jobSeekerResponseDTO.setAppliedJobs(appliedJobResponseDTOs);
		jobSeekerResponseDTO.setName(jobSeeker.getFirstName() + " " + jobSeeker.getLastName());

		String base64ProfilePhoto = Base64.getEncoder().encodeToString(jobSeeker.getProfilePhoto());
		jobSeekerResponseDTO.setProfilePhoto(base64ProfilePhoto);

		return jobSeekerResponseDTO;

	}

	@Override
	public byte[] getResume(String jobSeekerEmail) {
		JobSeeker jobSeeker = jobSeekerRepository.findByEmail(jobSeekerEmail)
				.orElseThrow(() -> new ApiCustomException("User Not Found!"));
		if (jobSeeker.getResume() == null)
			throw new ApiCustomException("Resume Not Uploaded!");
		return jobSeeker.getResume();
	}

	@Override
	public void deleteJobSeeker(String email) {
		JobSeeker jobSeeker = jobSeekerRepository.findByEmail(email)
				.orElseThrow(() -> new ApiCustomException("Jobseeker Does Not Exists!"));
		jobSeekerRepository.delete(jobSeeker);
	}

	@Override
	public boolean checkEmail(String email) {
		return jobSeekerRepository.existsJobSeekerByEmail(email);
	}

}
