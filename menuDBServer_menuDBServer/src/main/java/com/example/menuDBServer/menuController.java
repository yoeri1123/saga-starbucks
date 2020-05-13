package com.example.menuDBServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class menuController {
//    http://localhost:8000/menu/getMenuId/Ice_Americano
    @Autowired
    private menuDAO menuDAO;

    @Autowired
    private rabbitmqProducer rabbitmqProducer;

    @GetMapping("/getMenuId/{name}")
    public menu getMenuId(@PathVariable String name){
        try {
            menu me = menuDAO.findMenu(name);
            rabbitmqProducer.sendHello(me.getPk().toString());
            return me;
        }catch(Exception e){return null;}
    }

    @GetMapping("/getMenuAll")
    public Iterable<menu> getMenuAll(){
        return menuDAO.findAll();
    }


}
