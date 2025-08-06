package com.khademni.controller;

import com.khademni.dto.UserDTO;
import com.khademni.entity.User;
import com.khademni.service.UserService;
import com.khademni.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.signup(userDTO);
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
            return ResponseEntity.ok(new AuthResponse(token, user.getRole()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
            User user = userService.findByUsername(userDTO.getUsername());
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
            return ResponseEntity.ok(new AuthResponse(token, user.getRole()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    private static class AuthResponse {
        private String token;
        private String role;

        public AuthResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        public String getToken() { return token; }
        public String getRole() { return role; }
    }
}