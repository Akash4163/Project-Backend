package com.jobnexus;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.jobnexus.security.SecurityConfiguration;

@SpringBootApplication
public class Application {

	// Start Point
	public static void main(String[] args) {
		System.out.println("Hello"); 
		SpringApplication.run(Application.class, args);
		
	}

	@Bean
	public ModelMapper mapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT)
				.setPropertyCondition(Conditions.isNotNull());
		return modelMapper;
	}

}
/*You’re using @SpringBootApplication to define the entry point of your Spring Boot application 
 * and providing a ModelMapper bean configuration. Here’s a breakdown of what you have:


@SpringBootApplication: This annotation marks the main class of a Spring Boot application and 
enables component scanning, auto-configuration, and property support.

main method: This is the entry point of your application where SpringApplication.run() 
starts the Spring Boot application.

mapper method: This defines a ModelMapper bean, which is configured with a strict matching
 strategy and a condition to only map non-null properties. This is useful for mapping between 
 different object models, such as DTOs and entities.
*/
