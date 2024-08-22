package com.jobnexus.responseDTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterResponseDTO {

    // Basic recruiter information
    private String firstName; // Recruiter's first name
    private String username;  // Recruiter's username
    private String email;     // Recruiter's email address

    // Company details
    private String companyName;          // Name of the company
    private String companyUrl;           // URL of the company's website
    private String companyDescription;   // Description of the company
    private String companyAddress;       // Address of the company
    private String companyContact;       // Contact information for the company

    // Job-related information
    private List<JobResponseDTO> jobs;   // List of jobs associated with the recruiter
    private int totalApplicants;         // Total number of applicants for all jobs
    private int activeJobs;              // Number of active job postings
    private String companyLogoBase64;    // Base64 encoded string of the company logo
    private int totalHired;              // Total number of successful hires made by the recruiter
}

/*With DTOs:

Pros: Better separation of concerns, controlled data exposure, flexibility in formatting, and dedicated validation.
Cons: Requires additional code for mapping between DTOs and entities.
Without DTOs:

Pros: Simpler implementation, less code to maintain.
Cons: Direct coupling between API and database layer, potential security issues, less flexibility in response formatting
*/