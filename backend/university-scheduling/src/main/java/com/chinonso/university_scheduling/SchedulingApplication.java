package com.chinonso.university_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.repository.UserRepository;

@SpringBootApplication
public class SchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {

			String email = "admin@university.ac.uk";

			// check if admin already exists
			if (userRepository.findByEmail(email).isEmpty()) {

				User admin = new User();
				admin.setEmail(email);
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole(User.Role.ADMIN);
				admin.setIsActive(true);

				userRepository.save(admin);

				System.out.println("✅ Admin created successfully!");
			} else {
				System.out.println("⚠️ Admin already exists");
			}
		};
	}
}