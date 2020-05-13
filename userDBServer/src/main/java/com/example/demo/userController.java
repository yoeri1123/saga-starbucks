package com.example.demo;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    public userDAO userDAO;
    
    @Autowired
	private rabbitmqProducer rabbitmqProducer;
	
    @RabbitListener(queues = "q.yoeri")
    public void listen(String message){
    	
        System.out.println("Consumng : "+message);
        
        rabbitmqProducer.sendHello(message);
        user u =new user();
        u.setOrder_id(message);
        u.setUser_id("yoeri");
        userDAO.save(u);
    }
    
    
    
}
