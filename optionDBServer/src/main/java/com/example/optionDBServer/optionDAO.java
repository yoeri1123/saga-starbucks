package com.example.optionDBServer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface optionDAO extends CrudRepository<personal_option, Long> {

    @Query("select po from personal_option po where po.option_name = :option_name")
    personal_option findMenu(String option_name);
//    http://localhost:8001/option/getOptionId/One_Shot
    default personal_option findOne(Long id){
        return (personal_option) findById(id).orElse(null);
    }
}
