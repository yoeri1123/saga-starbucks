package com.example.demo;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Service
@RestController
@RequestMapping("/saga")
public class sagaController {
	
    @Autowired
    public sagaDAO sagaDAO;
    
    @Autowired
    sagaRabbitmqProducer rabbitmqProducer;
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @GetMapping("/saveEmptyOrder/{order_id}")
    public String saveEmptyOrder(@PathVariable String order_id) {
    	System.out.println("sagaManager "+order_id);
    	saga s = new saga();
    	s.setOrder_id(order_id);
    	s.setOrder_status("ORDER_EMPTY_PENDING");
    	try {
    		sagaDAO.save(s);
    		getAllOrder();
    		rabbitmqProducer.sendOrderEmpty(order_id);
    		return "OK";
    	}
    	catch(Exception e) {
    		System.out.println(e.toString());
    		return e.toString();
    	}
    }
    
    @GetMapping("/getAllOrder")
    public void getAllOrder() {
    	Iterable<saga> od=sagaDAO.findAll();
    	for(saga o:od) {
    		System.out.println(o.getOrder_id() +" "+ o.getOrder_status());
    	}
    }
    
    @RabbitListener(queues = "q.verifyEmptyOrder")
    public void verifyEmptyOrderListen(String message){
        sagaDAO.updateOrderStatus(message, "ORDER_VERIFY");
        this.getAllOrder();
    }

    @RabbitListener(queues = "q.rejectEmptyOrder")
    public void rejectEmptyOrderListen(String message){
        System.out.println("Consumng : "+message);
        sagaDAO.updateOrderStatus(message, "ORDER_VERIFY");
        this.getAllOrder();
    }

    
    @RabbitListener(queues = "q.verifyUpdateOrder")
    public void verifyUpdateOrderListen(String message){
        System.out.println("Consumng : "+message);
        sagaDAO.updateOrderStatus(message, "ORDER_VERIFY");
        this.getAllOrder();
    }

    

}
