package com.example.demo;

import lombok.*;

import javax.persistence.*;


@Builder
@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;
    
    @Column
    private String user_id;

    @Column
    private String order_id;

}
