package com.netease.nemo.entlive.parameter.neroomNotify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RoomMember {
    private String role;
    private String userUuid;
}
