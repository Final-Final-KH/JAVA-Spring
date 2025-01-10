package com.capstone.project.kedu.entity.edu;

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

    @Column(name = "region_name")  // DB 컬럼명은 그대로 두고
    private String regionName;     // 필드명만 regionName으로 변경

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistrictEntity2> districtEntity2 = new ArrayList<>();

}
