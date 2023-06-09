package com.netease.nemo.openApi.dto.neroom;

import lombok.Data;

import java.util.List;

@Data
public class NeRoomMemberDto {
    private Integer totalCount;
    private Integer pageTotal;
    private List<User> users;

    @Data
    public static class User {
        private String userUuid;
        private String name;
        private String role;
    }
}
