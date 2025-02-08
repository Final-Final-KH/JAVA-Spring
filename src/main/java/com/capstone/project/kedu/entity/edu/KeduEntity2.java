package com.capstone.project.kedu.entity.edu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "kedu")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class KeduEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long course_id;

    private String academy_name;

    private String course_name;

    private Date start_date;

    private Date end_date;

    private String region;

    private String auth;

    private int tr_date;

    private int total_hour;

    private int employment_rate;

    private int price_total;

    private int self_payment;

}
