package com.netease.nemo.entlive.parameter.neroomNotify;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinRoomEventNotify {
    private String roomArchiveId;
    private String roomUuid;
    private Set<RoomMember> users;
}
