package com.shop.auth.service;

import com.shop.auth.dto.JwtResponse;
import com.shop.auth.dto.LoginRequest;
import com.shop.auth.entity.Role;
import com.shop.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shop.auth.dto.RegisterRequest;
import com.shop.auth.entity.User;
import com.shop.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;

@Service // ← acesta lipsește sau este șters
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public JwtResponse login(LoginRequest dto) {
        // 1. autentificare
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        // 2. extragem UserDetails (conține și autoritățile)
        UserDetails user = (UserDetails) auth.getPrincipal();
        String role = user.getAuthorities().iterator().next().getAuthority(); // "ADMIN" sau "CUSTOMER"

        // 3. generăm token cu rolul real
        String token = jwtUtils.generateToken(user.getUsername(), role);

        return new JwtResponse(token, role);
    }

    public ResponseEntity<?> register(RegisterRequest dto) {

        String username = dto.username() == null ? "" : dto.username().trim();
        String password = dto.password();

        if (username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Username sau parola invalide");
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(409).body("Username deja există");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.CUSTOMER); // sau Role.USER dacă ai enum

        userRepository.save(user);

        return ResponseEntity.ok("User înregistrat cu succes");
    }

}