package com.example.demo.service.impl;

import com.example.demo.dto.UserLoginDTO;
import com.example.demo.entity.LoginLog;
import com.example.demo.entity.User;
import com.example.demo.mapper.AuthMapper;
import com.example.demo.common.Result;
import com.example.demo.service.AuthService;
import com.example.demo.common.Constants;
import com.example.demo.utils.IPUtil;
import com.example.demo.utils.JwtUtil;
import com.example.demo.vo.LoginLogVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthMapper authMapper;


    @Override
    public Result<Map<String, Object>> loginByEmail(UserLoginDTO loginDTO, HttpServletRequest request) {
        User user = authMapper.getUserByEmail(loginDTO.getEmail());
        if (user == null) {
            return Result.fail("用户名不存在");
        }

        if (user.getPasswordHash().equals(DigestUtils.md5DigestAsHex((loginDTO.getPassword()+ Constants.MD5_SALT).getBytes()) )) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            map.put("avatar", user.getAvatarUrl());
            String token = JwtUtil.createToken(map);
            map.put("token", token);
            String ipAddr = IPUtil.getIpAddr(request);
            log.info("用户登录ip地址:{}", ipAddr);
            String addr = IPUtil.getAddr(ipAddr);
            log.info("用户地址:{}", addr);
            LoginLog loginLog = LoginLog
                    .builder()
                    .userId(user.getId())
                    .ipAddress(ipAddr+"-"+addr)
                    .userAgent(request.getHeader("user-agent"))
                    .build();
            authMapper.saveLoginLog(loginLog);
            return Result.success(map);
        } else {
            return Result.fail("密码错误");
        }
    }

    @Override
    public Result<String> register(UserLoginDTO userLoginDTO) {
        if (authMapper.getUserByEmail(userLoginDTO.getEmail()) != null) {
            return Result.fail("邮箱已被注册");
        } else {
            User user = new User();
            user.setEmail(userLoginDTO.getEmail());
            user.setPasswordHash(DigestUtils.md5DigestAsHex((userLoginDTO.getPassword()+Constants.MD5_SALT).getBytes()));
            authMapper.insertUser(user);
            return Result.success("注册成功");
        }
    }

    @Override
    public Result<List<?>> getLoginLogs(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseJWT(authorization);
        String email = claims.get("email").toString();
        User user = authMapper.getUserByEmail(email);
        String userId = user.getId();
        log.info("userId:{}", userId);
        List<LoginLogVO> ipAddress = authMapper.getIpAddress(userId);
        log.info("ipAddress:{}", ipAddress);
        if (ipAddress == null) {
            return Result.success(Collections.singletonList("无结果"));
        }
        return Result.success(ipAddress);
    }


}
