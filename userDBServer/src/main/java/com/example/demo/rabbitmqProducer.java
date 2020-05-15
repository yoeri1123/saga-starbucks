package com.example.demo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class rabbitmqProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendHello(String content){
    	System.out.println("yoeri OK Send -> send queue");
        
    	try{
    		rabbitTemplate.convertAndSend("q.verfiyUser", content);
    	}
    	catch(Exception e) {
    		System.out.println(e.toString());
    	}
    }
    
    public void sendVerifyCreditUSer(String orderId) {
    	 rabbitTemplate.convertAndSend("q.verifyCreditUser", orderId);
    }
    
    public void sendRejectCreditUser(String orderId) {
   	 	rabbitTemplate.convertAndSend("q.rejectCreditUser", orderId);
    }
}