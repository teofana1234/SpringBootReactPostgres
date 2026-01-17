package com.shop.auth.controller;

import com.shop.auth.dto.LoginRequest;
import com.shop.auth.dto.JwtResponse;
import com.shop.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shop.auth.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest dto) {
        return authService.login(dto);
    }


    @PostMapping("/register")
    public JwtResponse register(@RequestBody RegisterRequest dto) {
        return authService.register(dto);
    }


}