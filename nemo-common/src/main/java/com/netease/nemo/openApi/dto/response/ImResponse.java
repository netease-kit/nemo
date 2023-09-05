package com.netease.nemo.openApi.dto.response;

import lombok.Data;
import lombok.ToString;

/**
 * IM服务请求返回对象
 */
@Data
@ToString
public class ImResponse {
    private Integer code;
    private String desc;
    private Object info;
    private Object data;
}
