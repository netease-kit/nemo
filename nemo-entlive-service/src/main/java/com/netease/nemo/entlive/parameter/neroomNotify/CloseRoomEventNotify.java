package com.netease.nemo.entlive.parameter.neroomNotify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseRoomEventNotify {
    private String roomArchiveId;
    private String roomUuid;
    private String operatorUserUuid;
}
