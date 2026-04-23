package com.lhsdev.cmsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CmsprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmsprojectApplication.class, args);
	}

}
