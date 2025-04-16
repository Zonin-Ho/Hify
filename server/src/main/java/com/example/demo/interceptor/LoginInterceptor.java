package com.example.demo.interceptor;

import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURL().toString();
        log.info("接口拦截，path:{}", path);

        String token = request.getHeader("Authorization");
        log.info("开始校验token:{}", token);
        if (token == null || token.isEmpty()) {
            log.info("token为空，请求被拦截");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        Claims claims = JwtUtil.parseJWT(token);
        if (claims == null) {
            log.warn("token无效，请求被拦截");
            throw new RuntimeException("token无效");
        } else {
            String email = claims.get("email", String.class);
            log.info("已登录，email:{}", email);
            return true;
        }
    }
}