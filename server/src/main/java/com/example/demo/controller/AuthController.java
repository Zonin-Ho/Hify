package com.example.demo.controller;

import com.example.demo.dto.UserLoginDTO;
import com.example.demo.common.Result;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> loginByEmail(@Validated @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        return authService.loginByEmail(userLoginDTO, request);
    }

    @PostMapping("/registry")
    public Result<String> registry(@Validated @RequestBody UserLoginDTO userLoginDTO) {
        return authService.register(userLoginDTO);
    }

    @GetMapping("getLoginLog")
    public Result<List<?>> getLoginLogs(HttpServletRequest request) {
        return authService.getLoginLogs(request);
    }
}
