package com.capstone.project.emailverification.controller;

import com.capstone.project.emailverification.dto.SendCodeRequest;
import com.capstone.project.emailverification.dto.VerifyRequestDTO;
import com.capstone.project.emailverification.service.EmailVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")

public class EmailVerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;


    @PostMapping("/send")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody SendCodeRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyRequestDTO request) {
        boolean isVerified = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("verified", isVerified));
    }
}