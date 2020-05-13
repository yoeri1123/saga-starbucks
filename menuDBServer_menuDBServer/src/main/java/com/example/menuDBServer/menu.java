package com.example.menuDBServer;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="menu")
public class menu {
    @Id
    private String pk;

    @Column(length = 100)
    private String menu_name;
}
