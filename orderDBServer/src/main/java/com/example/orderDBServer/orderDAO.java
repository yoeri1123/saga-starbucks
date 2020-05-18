package com.example.orderDBServer;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Transactional
public interface orderDAO extends CrudRepository<orders, Long> {
    
    @Query("select o from orders o where o.menu_id = :menu_id")
    Iterable<orders> findMenuId(String menu_id);

    @Query("select o from orders o where o.option_id = :option_id")
    Iterable<orders> findOptionId(String option_id);

    @Query("select o from orders o where o.user_id = :user_id")
    Iterable<orders> findUserId(String user_id);
    
    @Query("select o from orders o where o.order_id = :order_id")
    orders findOrderId(@Param("order_id") String order_id);
    
    @Query("select o from orders o where o.order_status = :order_status")
    Iterable<orders> findOrderStatus(String order_status);

    // , o.option_id= :option_id, o.order_status= :order_status 

    @Modifying
    @Query(value="update orders o set o.menu_id= :#{#order.menu_id}, o.option_id= :#{#order.option_id}, o.order_status= :#{#order.order_status} where o.order_id= :#{#order.order_id}",  nativeQuery=false)
    void updateOrder(@Param("order") orders order);
    
    @Modifying
    @Query(value="update orders o set o.order_status= :order_status where o.order_id= :order_id",  nativeQuery=false)
    void updateOrderStatus(@Param("order_id") String order_id, @Param("order_status") String order_status);
    
    @Modifying
    @Query(value="update orders o set o.datetime= :datetime, o.order_status= :order_status where o.order_id= :order_id",  nativeQuery=false)
    void updateOrderDatetime(@Param("order_id") String order_id, 
    		@Param("datetime") String datetime, 
    		@Param("order_status") String order_status);
    
    
    default orders findOne(Long id){
        return (orders) findById(id).orElse(null);
    }
    
    

}
