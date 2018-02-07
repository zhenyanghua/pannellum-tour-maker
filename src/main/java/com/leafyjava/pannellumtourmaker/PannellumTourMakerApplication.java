package com.leafyjava.pannellumtourmaker;

import com.leafyjava.pannellumtourmaker.storage.configs.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@RefreshScope
public class PannellumTourMakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PannellumTourMakerApplication.class, args);
	}
}
