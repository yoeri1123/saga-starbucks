package com.example.menuDBServer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface menuDAO extends CrudRepository<menu, String> {

    @Query("select m from menu m where m.menu_name = :menu_name")
    menu findMenu(String menu_name);

    default menu findOne(String id){
        return (menu) findById(id).orElse(null);
    }
}
