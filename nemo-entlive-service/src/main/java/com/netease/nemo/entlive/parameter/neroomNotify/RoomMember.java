package com.netease.nemo.entlive.parameter.neroomNotify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class RoomMember {
    private String role;
    private String userUuid;
}
