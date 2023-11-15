package com.netease.nemo.code;

/**
 * 忽然exception code
 * @Author：CH
 * @Date：2023/8/14 8:18 PM
 */
public enum SudErrorCodeEnum {
    SUCCESS(0, "成功"),
    TOKEN_CREATE_FAILED(1001, "Token创建失败"),
    TOKEN_VERIFY_FAILED(1002, "Token校验失败"),
    TOKEN_DECODE_FAILED(1003, "Token解析失败"),
    TOKEN_INVALID(1004, "Token非法"),
    TOKEN_EXPIRED(1005, "Token过期"),
    UNDEFINE(9999, "未知错误");

    private int code;
    private String msg;

    private SudErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
