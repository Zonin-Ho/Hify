package com.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    public Result(T data) {
        this.code = StateCode.SUCCESS;
        this.msg = "success";
        this.data = data;
    }

    public Result(T data, boolean success, String msg) {
        if (success) {
            this.code = StateCode.SUCCESS;
            this.msg = "success";
        } else {
            this.code = StateCode.FAIL;
            this.msg = msg;
        }
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(StateCode.FAIL, msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg);
    }
}

