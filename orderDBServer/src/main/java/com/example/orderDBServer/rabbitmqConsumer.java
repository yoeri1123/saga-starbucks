package com.example.orderDBServer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class rabbitmqConsumer {
	
    @Autowired
    public orderDAO orderDAO;
    
    
}
