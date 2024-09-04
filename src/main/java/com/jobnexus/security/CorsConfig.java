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

        // Allow specific origins
        config.addAllowedOriginPattern("https://www.jobnexus.in");
        config.addAllowedOriginPattern("https://jobnexus.in");
        config.addAllowedOriginPattern("https://*.vercel.app"); // To cover all Vercel subdomains
        config.addAllowedOriginPattern("https://*.railway.app"); // To cover all Railway subdomains
        config.addAllowedOriginPattern("http://localhost:3000"); // Local development

        // Allow all headers and methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
