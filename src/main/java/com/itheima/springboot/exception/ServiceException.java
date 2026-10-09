package com.itheima.springboot.exception;

import lombok.Getter;

/**
 * @Author YuanZG
 * @Date 2026/10/3
 * @Description ServiceException  自定义异常
 */
@Getter
public class ServiceException extends RuntimeException{
    private String code;
    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }
}
