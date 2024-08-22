package com.jobnexus.service; // Declares the package for this class

import java.util.ArrayList; // Imports ArrayList class for dynamic arrays
import java.util.Base64; // Imports Base64 class for encoding images
import java.util.List; // Imports List interface for working with lists

import javax.transaction.Transactional; // Imports Transactional annotation for managing transactions

import org.modelmapper.ModelMapper; // Imports ModelMapper class for object mapping
import org.springframework.beans.factory.annotation.Autowired; // Imports Autowired annotation for dependency injection
import org.springframework.stereotype.Service; // Imports Service annotation to mark this class as a service component

import com.jobnexus.entities.Experience; // Imports Experience entity class
import com.jobnexus.entities.Job; // Imports Job entity class
import com.jobnexus.entities.JobApplication; // Imports JobApplication entity class
import com.jobnexus.entities.JobCategory; // Imports JobCategory entity class
import com.jobnexus.entities.JobSeeker; // Imports JobSeeker entity class
import com.jobnexus.entities.Recruiter; // Imports Recruiter entity class
import com.jobnexus.exception.ApiCustomException; // Imports custom exception class for API errors
import com.jobnexus.repositories.JobCategoryRepository; // Imports repository interface for JobCategory
import com.jobnexus.repositories.JobRepository; // Imports repository interface for Job
import com.jobnexus.repositories.RecruiterRepository; // Imports repository interface for Recruiter
import com.jobnexus.requestDTO.JobRequestDTO; // Imports JobRequestDTO class for job creation request
import com.jobnexus.requestDTO.jobSeekerDTO.ExperienceDTO; // Imports ExperienceDTO class for job seeker experience
import com.jobnexus.responseDTO.ApplicantResponseDTO; // Imports ApplicantResponseDTO class for applicant details response
import com.jobnexus.responseDTO.JobListResponseDTO; // Imports JobListResponseDTO class for job listing response
import com.jobnexus.responseDTO.JobResponseDTO; // Imports JobResponseDTO class for job details response

@Transactional // Ensures that methods in this class are transactional
@Service // Marks this class as a Spring service component
public class JobServiceImpl implements JobService { // Implements the JobService interface

    @Autowired // Automatically injects the JobRepository dependency
    private JobRepository jobRepository;
    @Autowired // Automatically injects the RecruiterRepository dependency
    private RecruiterRepository recruiterRepository;
    @Autowired // Automatically injects the JobCategoryRepository dependency
    private JobCategoryRepository jobCategoryRepository;
    @Autowired // Automatically injects the ModelMapper dependency
    private ModelMapper mapper;

    @Override
    public List<JobListResponseDTO> getAllJobs() { // Retrieves all jobs
        List<Job> jobs = jobRepository.findAll(); // Fetches all job entities from the repository
        List<JobListResponseDTO> jobResponseDTOs = new ArrayList<>(); // Creates a list to hold job response DTOs
        for (Job job : jobs) { // Iterates over each job
            JobResponseDTO jobResponseDTO = mapper.map(job, JobResponseDTO.class); // Maps Job entity to JobResponseDTO
            jobResponseDTO = helperFillJobResponseDTO(job, jobResponseDTO); // Fills additional job details
            JobListResponseDTO jobListResponseDTO = mapper.map(jobResponseDTO, JobListResponseDTO.class); // Maps to JobListResponseDTO
            jobListResponseDTO.setCompanyName(job.getRecruiter().getCompanyName()); // Sets company name
            jobListResponseDTO.setCompanyAddress(job.getRecruiter().getCompanyAddr()); // Sets company address
            jobListResponseDTO.setCompanyUrl(job.getRecruiter().getCompanyUrl()); // Sets company URL
            jobListResponseDTO.setApplicantCount(job.getJobApplications().size()); // Sets the count of applicants
            String postedDate = job.getPostingDate().getDayOfMonth() + " "
                    + job.getPostingDate().getMonth().toString().toLowerCase() + " " + job.getPostingDate().getYear(); // Formats posting date
            jobListResponseDTO.setPostedDate(postedDate); // Sets formatted posting date
            if (job.getRecruiter().getCompanyLogo() != null) // Checks if company logo exists
                jobListResponseDTO.setCompanyLogo(Base64.getEncoder().encodeToString(job.getRecruiter().getCompanyLogo())); // Encodes company logo to Base64 string
            jobResponseDTOs.add(jobListResponseDTO); // Adds the job response DTO to the list
        }
        return jobResponseDTOs; // Returns the list of job response DTOs
    }

    @Override
    public String createJob(JobRequestDTO jobRequestDTO) { // Creates a new job
        Recruiter recruiter = recruiterRepository.findByEmail(jobRequestDTO.getRecruiterEmail()) // Finds recruiter by email
                .orElseThrow(() -> new ApiCustomException("Email Not Found!")); // Throws exception if recruiter not found
        JobCategory jobCategory = jobCategoryRepository.findByName(jobRequestDTO.getJobCategory()) // Finds job category by name
                .orElseThrow(() -> new ApiCustomException("Job Category Not Found!")); // Throws exception if category not found
        Job job = mapper.map(jobRequestDTO, Job.class); // Maps JobRequestDTO to Job entity

        recruiter.setJob(job); // Sets job for the recruiter
        jobCategory.setJob(job); // Sets job for the job category

        job.setCategory(jobCategory); // Sets job category for the job
        job.setRecruiter(recruiter); // Sets recruiter for the job

        jobRepository.save(job); // Saves the job entity to the repository
        return "Job Created"; // Returns success message
    }

    @Override
    public String deleteJob(String email, Integer id) { // Deletes a job by id
        Job job = jobRepository.findById(id).orElseThrow(() -> new ApiCustomException("Job Does Not Exists!")); // Finds job by id
        if (!job.getRecruiter().getEmail().equals(email)) // Checks if the email matches the recruiter's email
            throw new ApiCustomException("Job Does Not Belong to You!"); // Throws exception if unauthorized
        job.getRecruiter().deleteJob(job); // Removes job from the recruiter
        job.getCategory().deleteJob(job); // Removes job from the category
        jobRepository.delete(job); // Deletes job from the repository
        return "Deleted"; // Returns success message
    }

    @Override
    public JobResponseDTO getJobDetails(Integer id) { // Retrieves job details by id
        Job job = jobRepository.findById(id).orElseThrow(() -> new ApiCustomException("Job Does Not Exists")); // Finds job by id
        JobResponseDTO jobResponseDTO = mapper.map(job, JobResponseDTO.class); // Maps Job entity to JobResponseDTO

        return helperFillJobResponseDTO(job, jobResponseDTO); // Fills additional job details
    }

    @Override
    public List<ApplicantResponseDTO> getApplicantsOfJob(String email, Integer id) { // Retrieves applicants for a particular job
        Job job = jobRepository.findById(id).orElseThrow(() -> new ApiCustomException("Job Does Not Exists")); // Finds job by id
        if (!job.getRecruiter().getEmail().equals(email)) // Checks if the email matches the recruiter's email
            throw new ApiCustomException("UNAUTHORIZED ACCESS"); // Throws exception if unauthorized
        List<JobApplication> applications = job.getJobApplications(); // Retrieves job applications for the job
        List<ApplicantResponseDTO> applicants = new ArrayList<>(); // Creates a list to hold applicant response DTOs
        for (JobApplication application : applications) { // Iterates over each job application
            JobSeeker applicant = application.getJobSeeker(); // Retrieves job seeker (applicant)
            ApplicantResponseDTO applicantDto = mapper.map(applicant, ApplicantResponseDTO.class); // Maps JobSeeker to ApplicantResponseDTO

            applicantDto.setJobId(application.getId()); // Sets job id
            applicantDto.setCity(applicant.getAddress().getCity()); // Sets applicant's city
            applicantDto.setName(applicant.getFirstName() + " " + applicant.getLastName()); // Sets applicant's full name
            applicantDto.setStatus(application.getStatus()); // Sets application status

            // Byte to Base64 String -> Profile Photo
            String base64ProfileImage = "";
            if (applicant.getProfilePhoto() != null) // Checks if profile photo exists
                base64ProfileImage = Base64.getEncoder().encodeToString(applicant.getProfilePhoto()); // Encodes profile photo to Base64 string
            applicantDto.setProfilePhoto(base64ProfileImage); // Sets encoded profile photo

            // Mapping Experience of JobSeeker to List of Experience DTO
            List<ExperienceDTO> experienceDTOs = new ArrayList<>(); // Creates a list for experience DTOs
            for (Experience experience : applicant.getExperiences()) { // Iterates over each experience
                ExperienceDTO experienceDTO = mapper.map(experience, ExperienceDTO.class); // Maps Experience to ExperienceDTO
                experienceDTOs.add(experienceDTO); // Adds experience DTO to the list
            }
            applicantDto.setExperiences(experienceDTOs); // Sets list of experiences

            applicants.add(applicantDto); // Adds applicant DTO to the list
        }
        return applicants; // Returns the list of applicant response DTOs
    }

    // Helper Method to Fill Recruiter Name and Job Categories
    private JobResponseDTO helperFillJobResponseDTO(Job job, JobResponseDTO jobResponseDTO) {
        String recruiterFullName = job.getRecruiter().getFirstName(); // Gets recruiter's first name

        if (job.getRecruiter().getLastName() != "" || job.getRecruiter().getLastName() != null) // Checks if last name exists
            recruiterFullName = recruiterFullName + " " + job.getRecruiter().getLastName(); // Appends last name to full name

        jobResponseDTO.setRecruiterName(recruiterFullName); // Sets recruiter's full name in the response DTO
        jobResponseDTO.setJobCategory(job.getCategory().getName()); // Sets job category in the response DTO
        return jobResponseDTO; // Returns updated JobResponseDTO
    }
}
