package com.netease.nemo.exception;

public class BsException extends RuntimeException {
    private Integer code;
    private String msg;

    public BsException(int code) {
        this.code = code;
    }

    public BsException(int code, String msg) {
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
}
