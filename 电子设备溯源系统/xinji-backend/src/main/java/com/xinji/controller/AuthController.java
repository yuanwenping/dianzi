package com.xinji.controller;

import com.xinji.dto.LoginRequest;
import com.xinji.dto.LoginResponse;
import com.xinji.dto.R;
import com.xinji.dto.RegisterRequest;
import com.xinji.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return R.ok(response);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return R.ok();
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }
}
