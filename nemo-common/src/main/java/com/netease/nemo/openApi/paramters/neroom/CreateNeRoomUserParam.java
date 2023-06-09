package com.netease.nemo.openApi.paramters.neroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNeRoomUserParam {
    private String userToken;
    private String imToken;
    private String userName;
    private String icon;
    private Boolean assertNotExist;
    private Boolean updateOnConflict;
}
