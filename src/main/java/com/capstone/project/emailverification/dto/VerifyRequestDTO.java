package com.capstone.project.emailverification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRequestDTO {
    private String email;
    private String code;
}
