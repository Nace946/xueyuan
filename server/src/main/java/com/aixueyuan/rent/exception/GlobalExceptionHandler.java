package com.aixueyuan.rent.exception;

import com.aixueyuan.rent.common.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：保证接口始终返回统一结构，前端不会出现裸错误页
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public R<Object> handleBiz(BizException e) {
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Object> handleException(Exception e) {
        log.error("系统异常", e);
        return R.fail("服务异常，请稍后重试");
    }
}
