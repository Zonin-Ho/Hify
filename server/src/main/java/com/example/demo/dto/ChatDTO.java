package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatDTO implements Serializable {
    private String message;
    private String modelName;
}
