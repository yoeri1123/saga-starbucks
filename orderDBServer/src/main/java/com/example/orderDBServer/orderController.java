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

    //单捞磐 老包己 蜡瘤
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @PostMapping("/saveOrder")
    public String saveOrder(orders param){
//        try{
//        	System.out.println(param.getOrder_id());
//        	orders od=orderDAO.findOrderId(param.getOrder_id());
//        	
//        	System.out.println(od.getOrder_id());
//        	System.out.println(param.getMenu_id());
//        	orderDAO.updateOrder(param);
//        	getAllOrder();
//        	rabbitmqProducer.sendHello(param.getOrder_id());
//
//        }catch(Exception e){
//            System.out.println(e.toString());
//        }
//        return "";
    	return "NoComeIN";
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @GetMapping("/saveEmptyOrder/{order_id}")
    public String saveEmptyOrder(@PathVariable String order_id) {
    	System.out.println(order_id);
//    	orders od=new orders();
//    	od.setOrder_id(order_id);
//    	od.setOrder_status("ORDER_EMPTY");
//    	try {
//    		orderDAO.save(od);
//    		return "SUCCEESS";
//    	}
//    	catch(Exception e) {
//    		System.out.println(e.toString());
//    		return e.toString();
//    	}
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

    
    @RabbitListener(queues = "q.verfiyUser")
    public void listen(String message){
        System.out.println("Consumng : "+message);
        orderDAO.updateOrderStatus(message, "ORDER_VERIFY");
        this.getAllOrder();
    }
}
