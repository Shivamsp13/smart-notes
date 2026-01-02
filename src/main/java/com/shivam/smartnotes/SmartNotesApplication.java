package com.shivam.smartnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

////mvn clean compile->use this to check the condition of the app.
@SpringBootApplication
@EnableScheduling
public class SmartNotesApplication {
	public static void main(String[] args) {
		SpringApplication.run(SmartNotesApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
