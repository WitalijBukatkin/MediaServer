package ru.mediaserver.service.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServerApplication {
	public static void main(String[] args) throws InterruptedException {
		Thread.sleep(30 * 1000);
		SpringApplication.run(AuthServerApplication.class, args);
	}
}
