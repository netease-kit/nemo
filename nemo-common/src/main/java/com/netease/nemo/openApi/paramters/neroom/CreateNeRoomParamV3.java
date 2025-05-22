package com.netease.nemo.openApi.paramters.neroom;

import com.google.gson.annotations.SerializedName;
import lombok.Data;


@Data
public class CreateNeRoomParamV3 {

    @SerializedName("template_id")
    private Long templateId;

    @SerializedName("room_uuid")
    private String roomUuid;

    @SerializedName("room_name")
    private String roomName;


    private String password;

    @SerializedName("room_profile")
    private Integer roomProfile;


    @SerializedName("config")
    private RoomComponentConfigV3 config;

    @SerializedName("host_user_uuid")
    private String hostUserUuid;


    @Data
    public static class RoomComponentConfigV3 {
        /**
         * 是否开启白板
         */
        private Boolean whiteboard;
        /**
         * 是否开启聊天室
         */
        private Boolean chatroom;
        /**
         * 是否开启rtc
         */
        private Boolean rtc;
        /**
         * 是否开启sip
         */
        private Boolean sip;
        /**
         * 是否开启录制
         */
        private Boolean record;
        /**
         * 直播配置
         */
        private LiveConfig live;
        /**
         * 是否开启麦位
         */
        private SeatConfig seat;


        @Data
        public static class LiveConfig{
            /**
             * 是否开启直播
             */
            private Boolean enable;
        }

        @Data
        public static class SeatConfig{
            /**
             * 是否开启直播
             */
            private Boolean enable;

            @SerializedName("seat_count")
            private Integer seatCount;

            @SerializedName("apply_mode")
            private Integer applyMode;

            @SerializedName("invite_mode")
            private Integer inviteMode;

            @SerializedName("seat_version")
            private String seatVersion;
        }
    }
}
