package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private String id;
    private String username;
    private String passwordHash;
    private String email;
    private String avatarUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
