package com.jobnexus.controllers;

// Import necessary packages
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.jobnexus.exception.ApiCustomException;
import com.jobnexus.requestDTO.RecruiterRequestDTO;
import com.jobnexus.responseDTO.JwtResponeDTO;
import com.jobnexus.responseDTO.RecruiterResponseDTO;
import com.jobnexus.service.RecruiterService;
import com.jobnexus.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;

// Enable logging
@Slf4j//Added @Slf4j annotation for logging capabilities.
//You can now use log.info(), log.error(), etc., to log messages.

// Mark this class as a REST controller
@RestController
@RequestMapping("/recruiter") // Map HTTP requests to /recruiter path
public class RecruiterController {

    // Inject the RecruiterService to handle business logic
    @Autowired
    private RecruiterService recruiterService;

    // Inject the AuthenticationManager to handle authentication
    @Autowired
    private AuthenticationManager manager;

    // Inject JwtUtils to handle JWT operations
    @Autowired
    private JwtUtils utils;

    // Default constructor for debugging
    public RecruiterController() {
        System.out.println("Inn RecruiterController");
    }

    // Register a new Recruiter
    @PostMapping
    public ResponseEntity<?> registerRecruiter(@RequestBody RecruiterRequestDTO recruiterRequestDTO) {
        // Call the service to add a new recruiter and return a response with HTTP status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(recruiterService.addRecruiter(recruiterRequestDTO));
    }

    // Upload a company logo
    @PostMapping(value = "/image/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> saveCompanyLogo(@PathVariable int id,
            @RequestPart("companyLogo") MultipartFile companyLogo) throws IOException {
        // Call the service to upload the image and return a response with HTTP status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(recruiterService.uploadImage(id, companyLogo));
    }

    // Authenticate a recruiter and generate a JWT token
    @PostMapping("/authenticate")
    public ResponseEntity<?> signInValidation(@RequestParam("email") String email,
            @RequestParam("password") String password) {
        // Check if the email is valid
        if (!recruiterService.checkEmail(email)) {
            throw new ApiCustomException("Invalid Credentials");
        }
        // Authenticate the recruiter using email and password
        Authentication principal = manager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        // Generate a JWT token
        String jwtToken = utils.generateJwtToken(principal);
        // Return the JWT token in the response with HTTP status 200 (OK)
        return ResponseEntity.status(HttpStatus.OK).body(new JwtResponeDTO(jwtToken));
    }

    // Get a Recruiter by email from JWT token
    @GetMapping
    public ResponseEntity<?> getRecruiter() {
        // Retrieve the authentication object from the security context
        Authentication jwtParsedUser = SecurityContextHolder.getContext().getAuthentication();
        // Get the recruiter's details based on the email extracted from the JWT token
        RecruiterResponseDTO recruiterResponseDTO = recruiterService.getRecruiter(jwtParsedUser.getName());
        // Return the recruiter's details in the response with HTTP status 200 (OK)
        return ResponseEntity.status(HttpStatus.OK).body(recruiterResponseDTO);
    }

    // Delete a Recruiter by email from JWT token
    @DeleteMapping
    public ResponseEntity<?> deleteRecruiter() {
        // Retrieve the authentication object from the security context
        Authentication jwtParsedUser = SecurityContextHolder.getContext().getAuthentication();
        // Call the service to delete the recruiter based on the email extracted from the JWT token
        recruiterService.deleteRecruiter(jwtParsedUser.getName());
        // Return a response with HTTP status 200 (OK) and a "Deleted" message
        return ResponseEntity.ok().body("Deleted");
    }
}
