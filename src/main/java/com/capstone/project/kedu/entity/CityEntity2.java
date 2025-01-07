package com.capstone.project.kedu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "region")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CityEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long region_id;

    private String region_name;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistrictEntity2> districtEntity2 = new ArrayList<>();

}
