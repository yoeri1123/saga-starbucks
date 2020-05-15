package com.example.demo;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Service
@RestController
@RequestMapping("/saga")
public class sagaController {
	
	final String front_ip=System.getenv("FRONT_IP");
	final String front_port = System.getenv("FRONT_PORT");
	
	final String front_url = "http://"+ front_ip + ":" + front_port + "/orderSuccess";
	
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
    		return "SM_ORDER_EMPTY_PENDING_OK";
    	}
    	catch(Exception e) {
    		System.out.println(e.toString());
    		return e.toString();
    	}
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @PostMapping("/saveOrder")
    public String saveOrder(@RequestParam(value="menu_id") String menu_id, 
    		@RequestParam(value="option_id") String option_id, 
    		@RequestParam(value="order_id") String order_id){
        try{
        	System.out.println(order_id);
            sagaDAO.updateOrderStatus(order_id, "ORDER_UPDATE_PENDING");
        	getAllOrder();
            System.out.println(menu_id);
        	System.out.println(option_id);        	
        	rabbitmqProducer.sendUpdateOrder(order_id, menu_id, option_id);
        	return "SM_ORDER_UPDATE_PENDING_OK";
        }catch(Exception e){
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
    
    //verify -> gotofront() OK 준비완료
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.verifyEmptyOrder")
    public void verifyEmptyOrderListen(String message){
    	/*
    	 * if ) 이미 주문이 있을 경우에는 reject 를 태워야 함. */
        sagaDAO.updateOrderStatus(message, "ORDER_EMPTY_VERIFY");
        this.getAllOrder();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.rejectEmptyOrder")
    public void rejectEmptyOrderListen(String order_id){
        System.out.println("Consumng : "+order_id);
        sagaDAO.updateOrderStatus(order_id, "ORDER_EMPTY_REJECT");
        checkOrderStatus(order_id);
    }

    // 성공적으로 menuId와 optionId를 적재한 경우
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.verifyUpdateOrder")
    public void verifyUpdateOrderListen(String order_id){
        System.out.println("Consumng : "+order_id);
        sagaDAO.updateOrderStatus(order_id, "ORDER_UPDATE_VERIFY");
        this.getAllOrder();
		rabbitmqProducer.sendCreateCreditUser(order_id);
		
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.rejectUpdateOrder")
    public void rejectUpdateOrderListen(String order_id){
        System.out.println("Consumng : "+order_id);
        sagaDAO.updateOrderStatus(order_id, "ORDER_UPDATE_REJECT");
        this.getAllOrder();
    }
    
    @Transactional(isolation = Isolation.SERIALIZABLE)    
    @RabbitListener(queues = "q.verifyCreditUser")
    public void verifyCreditUserListen(String order_id){
    	System.out.println("=================================================");
    	System.out.println("VerifyCreditUser Queue Come");
    	System.out.println(front_url);
        System.out.println("Consumng : "+order_id);
        try {
            sagaDAO.updateOrderStatus(order_id, "ORDER_UPDATE_CREDIT_VERIFY");
            this.getAllOrder();
            checkOrderStatus(order_id);
        }catch(NullPointerException e) {
        	System.out.println(e.toString());
        	return ;
        }

    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)    
    @RabbitListener(queues = "q.rejectCreditUser")
    public void rejectCreditUserListen(String order_id){
        System.out.println("Consumng : "+order_id);
        sagaDAO.updateOrderStatus(order_id, "ORDER_UPDATE_CREDIT_REJECT");
        this.getAllOrder();
    }

    
    @Transactional(isolation = Isolation.SERIALIZABLE)    
    public void checkOrderStatus(String order_id) {
    	saga s = sagaDAO.findOrderId(order_id);
    	System.out.println(s.getOrder_status());
    	switch(s.getOrder_status()) {
    		case "ORDER_EMPTY_PENDING":
    			break;
    		case "ORDER_EMPTY_VERIFY":
    			break;
    		case "ORDER_EMPTY_REJECT":
    			sagaDAO.deleteOrder(order_id);
    			break;
    		case "ORDER_UPDATE_CREDIT_VERIFY":
    			System.out.println("order_credit_verify come?");
    			//HttpResponse httpresponse
    			HttpResponse response = Unirest.post(front_url)
    					.field("order_status", "ORDER_UPDATE_CREDIT_VERIFY")
    					.field("order_id", order_id).asString();
    			System.out.println(response.getBody().toString());
    	    	String[] strarr=response.getBody().toString().split("\\$");
    	    	System.out.println(strarr[0]+" "+strarr[1]);
    					
    			if (response.getBody().toString().equals("FRONT_OK")) {
            		sagaDAO.updateOrderStatus(order_id, "ORDER_SUCCESS"); 
    			}else {
    				// reject 태워야 함.
    			}

    			System.out.println("쏨?");
    			break;
    		default:	
    			break;
    	}
    }

}
