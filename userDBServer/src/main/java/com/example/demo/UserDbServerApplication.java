package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UserDbServerApplication{

	 @Autowired
	 private rabbitmqProducer rabbitmqProducer;


	public static void main(String[] args) {
		SpringApplication.run(UserDbServerApplication.class, args);
	}


}
