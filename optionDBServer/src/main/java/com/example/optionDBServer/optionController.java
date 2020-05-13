package com.example.optionDBServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/option")
public class optionController {
    @Autowired
    private optionDAO optionDAO;

    @Autowired
    private rabbitmqProducer rabbitmqProducer;

    @GetMapping("/getOptionId/{name}")
    public personal_option getOptionId(@PathVariable String name){
        try {
            System.out.println(name);
            personal_option oe = optionDAO.findMenu(name);
            rabbitmqProducer.sendHello(oe.getPk().toString());
            return oe;
        }catch(Exception e){return null;}
    }

    @GetMapping("/getOptionAll")
    public Iterable<personal_option> getOptionAll(){
        return optionDAO.findAll();
    }
}
