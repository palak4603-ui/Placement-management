package com.placement.placementmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Stateless REST API using HTTP Basic: no cookies, so no CSRF
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers("/api/hello").permitAll()

                        // Jobs: everyone logged in can read, only ADMIN can change
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**")
                        .hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/jobs/**").hasRole("ADMIN")

                        // Students: personal data, ADMIN only
                        .requestMatchers("/api/students/**").hasRole("ADMIN")

                        // Applications
                        // Listing everyone who applied to a job is ADMIN only
                        // (must come BEFORE the general GET rule below)
                        .requestMatchers(HttpMethod.GET, "/api/applications/job/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/applications")
                        .hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/applications/**")
                        .hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/applications/**").hasRole("ADMIN")

                        // Eligibility check
                        .requestMatchers("/api/eligibility/**")
                        .hasAnyRole("ADMIN", "STUDENT")

                        // Anything else must at least be logged in
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Passwords come from application.properties (which is gitignored),
    // so they are never committed to GitHub.
    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${app.security.admin-password}") String adminPassword,
            @Value("${app.security.student-password}") String studentPassword) {

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        UserDetails student = User.withUsername("student")
                .password(encoder.encode(studentPassword))
                .roles("STUDENT")
                .build();

        return new InMemoryUserDetailsManager(admin, student);
    }
}