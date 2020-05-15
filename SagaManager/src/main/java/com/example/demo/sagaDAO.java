package com.example.demo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;



public interface sagaDAO extends CrudRepository<saga, Long>{
	
	default saga findOne(Long id){
        return (saga) findById(id).orElse(null);
    }
	
    @Modifying
    @Query(value="update saga s set s.order_status= :order_status where s.order_id= :order_id",  nativeQuery=false)
    void updateOrderStatus(@Param("order_id") String order_id, @Param("order_status") String order_status);

    
    @Query("select s from saga s where s.order_id = :order_id")
    saga findOrderId(@Param("order_id") String order_id);
    
    @Modifying
    @Query(value="delete from saga s where s.order_id= :order_id",  nativeQuery=false)
    void deleteOrder(@Param("order_id") String order_id);
    

    
}
