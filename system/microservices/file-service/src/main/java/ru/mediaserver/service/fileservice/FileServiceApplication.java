package ru.mediaserver.service.fileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import ru.mediaserver.service.fileservice.configuration.FileServiceConfiguration;

@Configuration
@SpringBootApplication
public class FileServiceApplication {
	private final FileServiceConfiguration fileServiceConfiguration;

	public FileServiceApplication(FileServiceConfiguration fileServiceConfiguration) {
		this.fileServiceConfiguration = fileServiceConfiguration;
	}

	public static void main(String[] args) {
		SpringApplication.run(FileServiceApplication.class, args);
	}
}
