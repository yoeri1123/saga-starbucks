package com.example.orderDBServer;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column
    private String menu_id;

    @Column
    private String option_id;

    @Column
    private String user_id;
    
    @Column
    private String order_id;

    @Column(length = 100)
    private String datetime;
    
    @Column
    private String order_status;


}
