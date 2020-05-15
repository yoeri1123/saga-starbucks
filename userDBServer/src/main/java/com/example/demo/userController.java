package com.example.demo;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.createCreditUser")
    public void createCreditUserListen(String order_id) {
    	try {
    		// order_id 가 아예 없는 경우에만 적재하기
    		user u =userDAO.findOrderId(order_id);
    		System.out.println(u.getOrder_id());
    	}catch(Exception e) {
    		System.out.println(e.toString());
    		user u =new user();
	        u.setOrder_id(order_id);
	        u.setUser_id("yoeri");
	        try {
	        	userDAO.save(u);
	        	getAllOrder();
	        	rabbitmqProducer.sendVerifyCreditUSer(order_id);
	        }catch(Exception e1) {
	        	rabbitmqProducer.sendRejectCreditUser(order_id);
	        }
    	}
    }
    @GetMapping("/getAllOrder")
    public Iterable<user> getAllOrder() {
    	Iterable<user> od=userDAO.findAll();
    	for(user o:od) {
    		System.out.println(o.toString());
    	}
		return od;
    }

}
