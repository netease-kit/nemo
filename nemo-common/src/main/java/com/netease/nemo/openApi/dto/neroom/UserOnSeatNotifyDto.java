package com.netease.nemo.openApi.dto.neroom;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UserOnSeatNotifyDto {
    @SerializedName("room_archive_id")
    private String roomArchiveId;
    @SerializedName("user_uuid")
    private String userUuid;
    private Integer index;
    private SeatUser user;

    @Data
    public static class SeatUser {
        @SerializedName("user_uuid")
        private String userUuid;
        private String name;
        private String icon;
        @SerializedName("inviter_user_uuid")
        private String inviterUserUuid;
        @SerializedName("on_seat_type")
        private Integer onSeatType;
        private String ext;
    }
}
