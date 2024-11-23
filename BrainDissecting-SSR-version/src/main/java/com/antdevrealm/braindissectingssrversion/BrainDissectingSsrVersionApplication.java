package com.antdevrealm.braindissectingssrversion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BrainDissectingSsrVersionApplication {
	public static void main(String[] args) {
		SpringApplication.run(BrainDissectingSsrVersionApplication.class, args);
	}

}
