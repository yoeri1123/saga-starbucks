package com.example.optionDBServer;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="personal_option")
public class personal_option {
    @Id
    private String pk;

    @Column(length = 100)
    private String option_name;
}
