package com.netease.nemo.openApi.dto.sud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author：CH
 * @Date：2023/8/23 11:48 AM
 */
@Data
public class SudBaseResult<T> {

    /**
     * 响应码
     */
    @JsonProperty("ret_code")
    private int retCode;

    /**
     * 响应消息
     */
    @JsonProperty("ret_msg")
    private String retMsg;

    /**
     * 业务响应数据
     */
    @JsonProperty("data")
    private T data;
}
