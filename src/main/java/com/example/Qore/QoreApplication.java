package com.example.Qore;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
public class QoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(QoreApplication.class, args);
	}

	@PostConstruct
	public void init() {

		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
		System.out.println("Zona horaria configurada a: " + TimeZone.getDefault().getID());
	}
}
