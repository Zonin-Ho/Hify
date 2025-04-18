package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import com.example.demo.common.Result;

public interface UserService {
    Result<String> uploadAvatar(MultipartFile file);
    Result<?> updateUsername(String username);
}