package com.netease.nemo.entlive.parameter.neroomNotify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinRoomEventNotify {
    private String roomArchiveId;
    private String roomUuid;
    private Set<RoomMember> users;
}
