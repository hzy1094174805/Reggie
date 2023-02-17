package com.itheima.reggie.common;

/**
 * 自定义异常
 *
 * @author ilovend
 * @date 2023/02/17
 */
public class CustomException extends RuntimeException {
    /**
     * 自定义异常
     *
     * @param message 消息
     */
    public CustomException(String message) {
        super(message);
    }
}
