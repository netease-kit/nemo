package com.netease.nemo.exception;

import com.netease.nemo.code.SudErrorCodeEnum;

/**
 * 忽然API异常
 */
public class SudException extends RuntimeException {
    private Integer code;
    private SudErrorCodeEnum sudErrorCodeEnum;
    private String msg;

    public SudException(int code) {
        this.code = code;
    }

    public SudException(SudErrorCodeEnum sudErrorCodeEnum) {
        this.code = -1;
        this.msg = sudErrorCodeEnum.getMsg();
        this.sudErrorCodeEnum = sudErrorCodeEnum;
    }

    public SudException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SudErrorCodeEnum getSudErrorCodeEnum() {
        return sudErrorCodeEnum;
    }

    public void setSudErrorCodeEnum(SudErrorCodeEnum sudErrorCodeEnum) {
        this.sudErrorCodeEnum = sudErrorCodeEnum;
    }
}
