package com.NaverAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NaverApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NaverApiApplication.class, args);
	}

}
