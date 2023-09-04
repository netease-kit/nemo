package com.netease.nemo.parameter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class InitUserParam {
    private String userName;
    // 1：表示1V1, 2表示 语聊房
    private Integer sceneType;
    private String userUuid;
    private String imToken;
    private String userToken;
    private String icon;
}
