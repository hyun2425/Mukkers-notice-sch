package com.example.mukkersnoticesch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MukkersNoticeSchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MukkersNoticeSchApplication.class, args);
	}

}
