package com.capstone.project.kedu.entity.mypage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "day")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DayEntity2 {
    @Id
    private Long id;
}
