package com.example.menuDBServer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class rabbitmqProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendHello(String content){
        rabbitTemplate.convertAndSend("q.yoeri", "Menu "+content);
    }

}
