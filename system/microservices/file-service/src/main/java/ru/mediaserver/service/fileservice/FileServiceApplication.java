package ru.mediaserver.service.fileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@SpringBootApplication
public class FileServiceApplication {

	public static void main(String[] args) throws InterruptedException {
		Thread.sleep(1000 * 30);

		SpringApplication.run(FileServiceApplication.class, args);
	}
}
