package com.example.demo.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LoginLogVO {
    private String ipAddress;
    private String userAgent;
    private Timestamp loginAt;
}
