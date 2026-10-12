
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
                // Stateless REST API using HTTP Basic
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Public endpoint
                        .requestMatchers("/api/hello").permitAll()

                        // Jobs: authenticated users can read;
                        // only ADMIN can create, update, or delete
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**")
                        .hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/jobs/**")
                        .hasRole("ADMIN")

                        // Student records: ADMIN only
                        .requestMatchers("/api/students/**")
                        .hasRole("ADMIN")

                        // Only ADMIN can see all applicants for a job
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/job/**")
                        .hasRole("ADMIN")

                        // Students can apply; controller determines
                        // their student ID
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications")
                        .hasAnyRole("ADMIN", "STUDENT")

                        // ADMIN and STUDENT can read application endpoints;
                        // controller verifies student ownership
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/**")
                        .hasAnyRole("ADMIN", "STUDENT")

                        // Status updates, deletions, and all other
                        // application operations are ADMIN only
                        .requestMatchers("/api/applications/**")
                        .hasRole("ADMIN")

                        // Eligibility checks
                        .requestMatchers("/api/eligibility/**")
                        .hasAnyRole("ADMIN", "STUDENT")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Passwords are loaded from application.properties
    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${app.security.admin-password}")
            String adminPassword,
            @Value("${app.security.student-password}")
            String studentPassword) {

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
