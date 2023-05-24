package com.netease.nemo.socialchat.parameter;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetUserInfoParam {
    @NotEmpty(message = "{userUuid.notNull}")
    private String userUuid;
    private String deviceId;
}
