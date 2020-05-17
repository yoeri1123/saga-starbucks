package com.example.orderDBServer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class rabbitmqProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    
    public void sendVerifyEmptyOrder(String orderId){
        rabbitTemplate.convertAndSend("q.verifyEmptyOrder", orderId);
    }
    
    public void sendVerifyOrder(String orderId) {
    	rabbitTemplate.convertAndSend("q.verifyUpdateOrder", orderId);
    }
    
    public void sendRejectOrder(String orderId) {
    	
    }
    public void sendCompVerifyRejectOrder(String orderId) {
    	rabbitTemplate.convertAndSend("q.compVerifyRejectOrder", orderId);
    }
}