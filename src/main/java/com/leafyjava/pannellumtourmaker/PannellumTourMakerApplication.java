package com.leafyjava.pannellumtourmaker;

import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class PannellumTourMakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PannellumTourMakerApplication.class, args);
	}

//	@Bean
//	CommandLineRunner init(StorageService storageService) {
//		return (args -> {
//			storageService.deleteAll();
//			storageService.init();
//		});
//	}
}
