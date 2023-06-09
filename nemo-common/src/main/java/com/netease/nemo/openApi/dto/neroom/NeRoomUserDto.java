package com.netease.nemo.openApi.dto.neroom;

import lombok.Data;

@Data
public class NeRoomUserDto {
    private String userName;
    private String icon;
    private String userUuid;
    private String userToken;
    private String imToken;
    private Long rtcUid;
    private String imKey;
    private String rtcKey;
}
