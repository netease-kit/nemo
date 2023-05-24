package com.netease.nemo.openApi.dto.nim;

import lombok.Data;

@Data
public class ImMsgDto {
    private Object msg;

    public ImMsgDto(Object msg) {
        this.msg = msg;
    }
}
