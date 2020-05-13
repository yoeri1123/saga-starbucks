package com.example.menuDBServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class MenuDbServerApplication{

	@Autowired
	private rabbitmqProducer rabbitmqProducer;

	public static void main(String[] args) {
		SpringApplication.run(MenuDbServerApplication.class, args);
	}

//	@Override
//	public void run(String... args){
//		rabbitmqProducer.sendHello("yoyoyo");
//	}

}
