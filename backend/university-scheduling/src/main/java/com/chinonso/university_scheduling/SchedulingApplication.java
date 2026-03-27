package com.chinonso.university_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.repository.UserRepository;

@SpringBootApplication
public class SchedulingApplication {

	private static final Logger log = LoggerFactory.getLogger(SchedulingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SchedulingApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			com.chinonso.university_scheduling.repository.TeacherRepository teacherRepository,
			com.chinonso.university_scheduling.repository.StudentRepository studentRepository) {
		return args -> {
			String adminEmail = "admin@university.ac.uk";
			if (userRepository.findByEmail(adminEmail).isEmpty()) {
				User admin = new User();
				admin.setEmail(adminEmail);
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole(User.Role.ADMIN);
				admin.setIsActive(true);
				userRepository.save(admin);
				log.info("Admin account initialised.");
			}

			for (com.chinonso.university_scheduling.entity.Teacher t : teacherRepository.findAll()) {
				if (userRepository.findByEmail(t.getEmail()).isEmpty()) {
					User teacherUser = new User();
					teacherUser.setEmail(t.getEmail());
					teacherUser.setPassword(passwordEncoder.encode("password123"));
					teacherUser.setRole(User.Role.TEACHER);
					teacherUser.setLinkedId(t.getTeacherId().longValue());
					teacherUser.setIsActive(true);
					userRepository.save(teacherUser);
				}
			}

			for (com.chinonso.university_scheduling.entity.Student s : studentRepository.findAll()) {
				if (userRepository.findByEmail(s.getEmail()).isEmpty()) {
					User studentUser = new User();
					studentUser.setEmail(s.getEmail());
					studentUser.setPassword(passwordEncoder.encode("password123"));
					studentUser.setRole(User.Role.STUDENT);
					studentUser.setLinkedId(s.getStudentId().longValue());
					studentUser.setIsActive(true);
					userRepository.save(studentUser);
				}
			}

			log.info("Startup seed check complete.");
		};
	}
}