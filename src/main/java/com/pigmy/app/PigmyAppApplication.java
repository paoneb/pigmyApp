package com.pigmy.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class PigmyAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigmyAppApplication.class, args);
	}

}

