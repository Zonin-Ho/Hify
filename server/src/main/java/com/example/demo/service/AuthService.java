package com.example.demo.service;

import com.example.demo.dto.UserLoginDTO;
import com.example.demo.entity.LoginLog;
import com.example.demo.common.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface AuthService {
    Result<Map<String, Object>> loginByEmail(UserLoginDTO loginDTO, HttpServletRequest request);

    Result<String> register(UserLoginDTO userLoginDTO);

    Result<List<?>> getLoginLogs(HttpServletRequest request);
}
