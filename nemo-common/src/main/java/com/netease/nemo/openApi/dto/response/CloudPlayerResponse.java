package com.netease.nemo.openApi.dto.response;

import lombok.Data;

@Data
public class CloudPlayerResponse {
    private Integer code;
    private String errmsg;
    private String requestId;
    private Object result;
}
