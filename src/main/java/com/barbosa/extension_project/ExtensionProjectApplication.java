package com.barbosa.extension_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.barbosa.extension_project")
@EnableJpaRepositories("com.barbosa.extension_project.infrastructure.persistence.repository")
public class ExtensionProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExtensionProjectApplication.class, args);
	}

}
