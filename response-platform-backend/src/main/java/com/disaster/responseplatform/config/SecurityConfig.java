//package com.disaster.responseplatform.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.http.HttpMethod;
//
//@Configuration
//public class SecurityConfig {
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf().disable()
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers(HttpMethod.GET, "/test").permitAll()
//                 .requestMatchers(HttpMethod.POST, "/api/reports/**").authenticated()
//                 .anyRequest().permitAll()
//             )
//             .httpBasic();
//         return http.build();
//     }
//}