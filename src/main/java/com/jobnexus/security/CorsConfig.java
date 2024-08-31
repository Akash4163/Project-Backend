package com.jobnexus.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		  // config.addAllowedOrigin("http://www.jobnexus.in"); // Your production domain
    //     config.addAllowedOrigin("http://localhost:3000"); // Your local development domain
    //     config.addAllowedOrigin("https://www.jobnexus.in");
    //     config.addAllowedOrigin("https://jobnexus.in");
		config.addAllowedOrigin("https://jobnexus.vercel.app");
		 config.addAllowedOrigin("https://jobnexus-hnapebnb8-akashs-projects-eadcd1b0.vercel.app");
        config.addAllowedOrigin("https://cozy-joy-production.up.railway.app"); // Railway domain
		
		
		
		// Adjust the origin as needed
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
