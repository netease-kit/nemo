package com.netease.nemo.openApi.dto.response;

import lombok.Data;

/**
 * NeRoom服务返回对象
 */
@Data
public class NeRoomResponse {
    private Object data;
    private Integer code;
    private String requestId;
    private String msg;
}
