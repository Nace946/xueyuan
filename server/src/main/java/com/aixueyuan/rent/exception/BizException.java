package com.aixueyuan.rent.exception;

/**
 * 业务异常（可预期的错误，如参数校验失败、数据不存在）
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BizException(String message) {
        super(message);
    }
}
