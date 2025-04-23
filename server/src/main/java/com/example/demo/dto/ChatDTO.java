package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatDTO implements Serializable {
    @NotBlank
    private String message;
    private String modelName;
    private String platform;
}
