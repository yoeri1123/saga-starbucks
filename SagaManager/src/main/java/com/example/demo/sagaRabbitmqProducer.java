package com.example.demo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class sagaRabbitmqProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendOrderEmpty(String orderId){
        rabbitTemplate.convertAndSend("q.createEmptyOrder", orderId);
    }
    
    public void sendUpdateOrder(String orderId, String menuId, String optionId) {
    	String message=orderId+"$"+menuId+"$"+optionId;
        rabbitTemplate.convertAndSend("q.updateOrder", message);
    }
    
    //Compensting Transaction
    public void rejectUpdateOrder(String orderId) {
        rabbitTemplate.convertAndSend("q.createEmptyOrder", orderId);    	
    }
    
    

}
