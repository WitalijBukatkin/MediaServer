package ru.mediaserver.service.downloadservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@SpringBootApplication
public class DownloadServiceApplication {

	public static void main(String[] args) throws InterruptedException {
		//TODO: Thread.sleep(1000 * 30);

		SpringApplication.run(DownloadServiceApplication.class, args);
	}
}
