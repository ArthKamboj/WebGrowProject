//package com.example.webgrow.configuration;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//    @Configuration
//    public class GlobalCorsConfig {
//
//        @Bean
//        public CorsFilter corsFilter() {
//            CorsConfiguration config = new CorsConfiguration();
//            config.addAllowedOrigin("*"); // Replace with allowed origin
//            config.addAllowedMethod("*"); // Allows all HTTP methods
//            config.addAllowedHeader("*"); // Allows all headers
//            config.setAllowCredentials(false); // Allows cookies
//
//            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//            source.registerCorsConfiguration("/**", config);
//            return new CorsFilter(source);
//        }
//    }
