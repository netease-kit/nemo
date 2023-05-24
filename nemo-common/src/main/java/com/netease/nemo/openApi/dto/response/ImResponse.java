package com.netease.nemo.openApi.dto.response;

import lombok.Data;

/**
 * IM服务请求返回对象
 */
@Data
public class ImResponse {
    private Integer code;
    private String desc;
    private Object info;
    private Object data;
}
