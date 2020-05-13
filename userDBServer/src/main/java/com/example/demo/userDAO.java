package com.example.demo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


@Transactional
public interface userDAO extends CrudRepository<user, Long>{
    
	@Query("select u from user u where u.order_id = :order_id")
    user findOrderId(@Param("order_id") String order_id);

	default user findOne(Long id){
        return (user) findById(id).orElse(null);
    }
}
