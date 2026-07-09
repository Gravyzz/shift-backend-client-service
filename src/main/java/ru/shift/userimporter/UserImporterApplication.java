package ru.shift.userimporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UserImporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserImporterApplication.class, args);
	}

}
