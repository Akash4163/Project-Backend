package com.jobnexus.service;

import javax.transaction.Transactional; // Import for managing transactions

import org.modelmapper.ModelMapper; // Import for mapping between different object models
import org.springframework.beans.factory.annotation.Autowired; // Import for automatic dependency injection
import org.springframework.mail.SimpleMailMessage; // Import for creating simple email messages
import org.springframework.mail.javamail.JavaMailSender; // Import for sending emails
import org.springframework.stereotype.Service; // Import to mark this class as a service component in Spring

import com.jobnexus.entities.Job; // Import for Job entity
import com.jobnexus.entities.JobApplication; // Import for JobApplication entity
import com.jobnexus.entities.JobSeeker; // Import for JobSeeker entity
import com.jobnexus.entities.Recruiter; // Import for Recruiter entity
import com.jobnexus.exception.ApiCustomException; // Import for custom exception handling
import com.jobnexus.repositories.JobApplicationRepository; // Import for repository handling JobApplication entities
import com.jobnexus.repositories.JobRepository; // Import for repository handling Job entities
import com.jobnexus.repositories.JobSeekerRepository; // Import for repository handling JobSeeker entities
import com.jobnexus.repositories.RecruiterRepository; // Import for repository handling Recruiter entities

@Service // Annotation to mark this class as a Spring service component
@Transactional // Annotation to manage transactions automatically for all methods
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    private ModelMapper mapper; // Automatically inject ModelMapper for object mapping

    @Autowired
    private JobApplicationRepository jobApplicationRepository; // Automatically inject repository for JobApplication

    @Autowired
    private JobRepository jobRepository; // Automatically inject repository for Job

    @Autowired
    private JobSeekerRepository jobSeekerRepository; // Automatically inject repository for JobSeeker

    @Autowired
    private RecruiterRepository recruiterRepository; // Automatically inject repository for Recruiter

    @Autowired
    private JavaMailSender javaMailSender; // Automatically inject JavaMailSender for sending emails

    @Override
    public String applyForJob(String jobSeekerEmail, Integer jobId) {
        // Find the JobSeeker by their email
        JobSeeker jobSeeker = jobSeekerRepository.findByEmail(jobSeekerEmail)
                .orElseThrow(() -> new ApiCustomException("Job Seeker Not Found!"));
        
        // Find the Job by its ID
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiCustomException("Job Does Not Exist"));

        // Create a new JobApplication instance
        JobApplication jobApplication = new JobApplication();
        jobApplication.setJobSeeker(jobSeeker); // Set the JobSeeker for the application
        jobApplication.setJob(job); // Set the Job for the application

        // Save the JobApplication to the database
        jobApplicationRepository.save(jobApplication);

        return "Applied"; // Return a success message
    }

    @Override
    public void hireJobSeeker(Integer jobApplicationId) {
        // Find the JobApplication by its ID
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ApiCustomException("Job Application Not Found"));

        // Check if the JobApplication status is already "HIRED"
        if ("HIRED".equals(jobApplication.getStatus())) {
            throw new ApiCustomException("Already Hired");
        }

        // Update the status to "HIRED"
        jobApplication.setStatus("HIRED");

        // Get the JobSeeker and Job from the JobApplication
        JobSeeker jobSeeker = jobApplication.getJobSeeker();
        Job job = jobApplication.getJob();

        // Find the Recruiter associated with the Job
        Recruiter recruiter = recruiterRepository.findById(job.getRecruiter().getId())
                .orElseThrow(() -> new ApiCustomException("Recruiter Not Found"));

        // Send an email notification to the JobSeeker
        sendHiringNotificationEmail(jobSeeker.getEmail(), job, jobSeeker, recruiter);

        // Send an email notification to the Recruiter
        sendRecruiterNotificationEmail(recruiter.getEmail(), job, jobSeeker);

        // Save the updated JobApplication
        jobApplicationRepository.save(jobApplication);
    }

    @Override
    public void changeStatus(Integer jobApplicationId, String status) {
        // Find the JobApplication by its ID
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ApiCustomException("Job Application Not Found"));

        // If the status is "HIRED", do not change it
        if ("HIRED".equals(jobApplication.getStatus())) {
            return;
        }

        // Update the status of the JobApplication
        jobApplication.setStatus(status);

        // Save the updated JobApplication
        jobApplicationRepository.save(jobApplication);
    }

    private void sendHiringNotificationEmail(String recipientEmail, Job job, JobSeeker jobSeeker, Recruiter recruiter) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject("Congratulations! You've Been Hired as " + job.getRole());

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear ").append(jobSeeker.getFirstName()).append(",\n\n");
        emailBody.append("We are thrilled to extend this offer of employment to you! ")
                .append("Congratulations on being selected for the position of ").append(job.getRole())
                .append(" at ").append(recruiter.getCompanyName()).append(".\n\n");
        emailBody.append("Here are the details of your new position:\n\n");
        emailBody.append("Role: ").append(job.getRole()).append("\n");
        emailBody.append("Company: ").append(recruiter.getCompanyName()).append("\n");
        emailBody.append("Location: ").append(job.getJobCity()).append("\n");
        emailBody.append("Start Date: We will send details soon\n\n");
        emailBody.append("We are confident that your skills and experience will be an invaluable addition to our team, ")
                .append("and we look forward to seeing the great work you'll do.\n\n");
        emailBody.append("Please let us know if you have any questions or need further information before your start date. ")
                .append("We're here to support you as you transition into your new role.\n\n");
        emailBody.append("Once again, congratulations, and welcome to ").append(recruiter.getCompanyName()).append("!\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append(recruiter.getFirstName());
        emailBody.append(recruiter.getLastName());// Replace with the actual name or fetch from configuration
        emailBody.append("Human resources"); // Replace with the actual position or fetch from configuration
        emailBody.append(recruiter.getCompanyName()); // Replace with actual contact information

        mailMessage.setText(emailBody.toString());
        javaMailSender.send(mailMessage);
    }


    private void sendRecruiterNotificationEmail(String recipientEmail, Job job, JobSeeker jobSeeker) {
        // Create a new SimpleMailMessage for the email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail); // Set the recipient's email address
        mailMessage.setSubject("New Hire Notification"); // Set the subject of the email
        mailMessage.setText("Dear Recruiter,\n\n" +
                "The job seeker " + jobSeeker.getFirstName() +""+ jobSeeker.getLastName() + " has been hired for the position of " +
                job.getRole() + ".\n\n" ); // Set the body of the email

        // Send the email
        javaMailSender.send(mailMessage);
    }
}
