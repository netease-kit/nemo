package com.netease.nemo.openApi.dto.neroom;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UserOffSeatNotifyDto {
    @SerializedName("room_archive_id")
    private String roomArchiveId;
    @SerializedName("user_uuid")
    private String userUuid;
    private Integer index;
}
