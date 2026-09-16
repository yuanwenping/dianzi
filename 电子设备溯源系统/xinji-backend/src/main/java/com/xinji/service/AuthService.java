package com.xinji.service;

import com.xinji.dto.LoginRequest;
import com.xinji.dto.LoginResponse;
import com.xinji.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void register(RegisterRequest request);
}
