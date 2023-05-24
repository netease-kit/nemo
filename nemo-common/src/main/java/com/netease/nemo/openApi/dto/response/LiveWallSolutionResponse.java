package com.netease.nemo.openApi.dto.response;

import lombok.Data;

/**
 * 安全通服务返回对象
 */
@Data
public class LiveWallSolutionResponse {
    private Integer code;
    private String msg;
    private Object result;
}
