package com.example.demo.exception;

import com.example.demo.common.Result;
import dev.langchain4j.exception.HttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    // 运行时错误
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<?> RuntimeExceptionHandler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Result.fail("系统错误，请稍后重试");
    }

    // 请求路径错误
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public Result<?> NoResourceFoundExceptionHandler() {
        return Result.fail(404, "404 NOT_FOUND");
    }

    // 缺少参数
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result<?> MissingServletRequestParameterExceptionHandler() {
        return Result.fail(400, "缺少必要参数");
    }

    // 参数校验错误
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<?> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String msg = fieldErrors.get(0).getDefaultMessage();
        log.error("参数错误：{}", msg);
        return Result.fail(400, msg);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<?> BusinessExceptionHandler(BusinessException e) {
        String msg = e.getMessage();
        log.error("系统错误：{}", msg);
        return Result.fail(500, msg);
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public Result<?> IOExceptionHandler(IOException e) {
        String msg = e.getMessage();
        log.error("输出错误：{}", msg);
        return Result.fail(500, msg);
    }

    @ExceptionHandler(HttpException.class)
    @ResponseBody
    public Result<?> HttpExceptionHandler(HttpException e) {
        String msg = e.getMessage();
        log.error("网络连接错误：{}", msg);
        return Result.fail(500, msg);
    }
}
