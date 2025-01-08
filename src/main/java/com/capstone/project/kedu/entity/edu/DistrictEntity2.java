package com.capstone.project.kedu.entity.edu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "district")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DistrictEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long district_id;

    private String district_name;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private CityEntity2 city;
}
