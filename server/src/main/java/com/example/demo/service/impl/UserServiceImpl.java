package com.example.demo.service.impl;

import com.example.demo.common.Result;
import com.example.demo.service.UserService;
import org.springframework.web.multipart.MultipartFile;

public class UserServiceImpl implements UserService {

    @Override
    public Result<String> uploadAvatar(MultipartFile file) {
        return null;
    }

    @Override
    public Result<?> updateUsername(String username) {
        return null;
    }
}
