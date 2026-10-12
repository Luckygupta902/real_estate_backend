package com.realestate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/*
 * @ComponentScan("com.realestate")
 * 
 * @EntityScan("com.realestate.entity")
 * 
 * @EnableJpaRepositories("com.realestate.repository")
 */
@SpringBootApplication
@EnableAsync
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

}
