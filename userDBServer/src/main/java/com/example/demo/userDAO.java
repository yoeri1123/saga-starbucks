package com.example.demo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


@Transactional
public interface userDAO extends CrudRepository<user, Long>{
    
	@Query("select u from user u where u.order_id = :order_id")
    user findOrderId(@Param("order_id") String order_id);

    @Modifying
    @Query(value="update user u set u.credit_status= :credit_status where u.order_id= :order_id",  nativeQuery=false)
    void updateOrderStatus(@Param("order_id") String order_id, @Param("credit_status") String credit_status);
    
    @Modifying
    @Query(value="delete from user u where u.order_id= :order_id",  nativeQuery=false)
    void deleteOrder(@Param("order_id") String order_id);
    
	default user findOne(Long id){
        return (user) findById(id).orElse(null);
    }
}
