package com.example.optionDBServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OptionDbServerApplication{
	@Autowired
	private rabbitmqProducer rabbitmqProducer;

	public static void main(String[] args) {
		SpringApplication.run(OptionDbServerApplication.class, args);
	}

//	@Override
//	public void run(String... args){
//		rabbitmqProducer.sendHello("yoyoyo");
//	}
}
