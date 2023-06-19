package com.netease.nemo.openApi.paramters.neroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NeRoomMessageParam {
    private String roomUuid;
    private String message;
}
