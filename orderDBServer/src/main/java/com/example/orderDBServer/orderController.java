package com.example.orderDBServer;



import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Service
@RestController
@RequestMapping("/order")
public class orderController {
    @Autowired
    public orderDAO orderDAO;
    
    @Autowired
    rabbitmqProducer rabbitmqProducer;

    //ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ ï¿½Ï°ï¿½ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @PostMapping("/saveOrder")
    public String saveOrder(orders param){
    	return "NoComeIN";
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @GetMapping("/saveEmptyOrder/{order_id}")
    public String saveEmptyOrder(@PathVariable String order_id) {
    	System.out.println(order_id);
    	return "NoComeIN";
    }
    
    @GetMapping("/getAllOrder")
    public Iterable<orders> getAllOrder() {
    	Iterable<orders> od=orderDAO.findAll();
    	for(orders o:od) {
    		System.out.println(o.getOrder_id() +" "+ o.getOrder_status());
    	}
    	return od;
    }

    //setEmptyOrder
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.createEmptyOrder")
    public void verifyEmptyOrderListen(String order_id) {
        System.out.println("Consumng : "+order_id);
    	orders od=new orders();
    	od.setOrder_id(order_id);
    	try {
    		orderDAO.save(od);
    		getAllOrder();
    		rabbitmqProducer.sendVerifyEmptyOrder(order_id);
    	}
    	catch(Exception e) {
    		System.out.println(e.toString());
    	}

        
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.updateOrder")
    public void updateOrderListen(String message) {
    	String[] strarr=message.split("\\$");
    	System.out.println(strarr[0]+" "+strarr[1]+" "+strarr[2]);
    	try {
    		orders o = orderDAO.findOrderId(strarr[0]);
    		System.out.println(o.toString());
    		o.setMenu_id(strarr[1]);
    		o.setOption_id(strarr[2]);
    		orderDAO.updateOrder(o);
    		rabbitmqProducer.sendVerifyOrder(strarr[0]);
    	}catch(Exception e) {
    		System.out.println(e.toString());
    		
    	}
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.compUpdateUser")
    public void compUpdateUserListen(String order_id) {
    	// reject credit update, order reject Ã³¸® ÈÄ, front·Î °¡¼­ °Å·¡ Ãë¼Ò‰ç´Ù°í ¸»ÇØÁà¾ß ÇÔ. 
        System.out.println("Consumng : "+order_id);
        orderDAO.updateOrderStatus(order_id, "ORDER_REJECT");
        this.getAllOrder();
        rabbitmqProducer.sendCompVerifyRejectOrder(order_id);
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.updateOrderDatetime")
    public void UpdateOrderDatetimeListen(String message) {
    	// order success ÈÄ, ÃÖÁ¾ datetime ÀúÀåÇØ¾ß ÇÔ. 
        System.out.println("Consumng : "+message);
    	String[] strarr=message.split("\\$");
    	// datetime°ú status ¸¦ ÀúÀåÇÑ´Ù. 
        orderDAO.updateOrderDatetime(strarr[1], strarr[0], "ORDER_SUCCESS");
        this.getAllOrder();
    }
    
    
    
    @RabbitListener(queues = "q.verfiyUser")
    public void listen(String message){
        System.out.println("Consumng : "+message);
        orderDAO.updateOrderStatus(message, "ORDER_VERIFY");
        this.getAllOrder();
    }
}
