package com.netease.nemo.openApi.paramters;

import lombok.Data;

@Data
public class RtcCloudPlayerTaskParam {
    private String cname;
    private Long cid;
    private String requestId;
    private Integer taskType;
    private CloudPlayerTask data;


    @Data
    public static class CloudPlayerTask {
        private String streamUrl; // 实时媒体流直播拉流地址，或音视频文件点播地址。支持如下协议和格式：协议：HTTP、HTTPS、RTMP、RTSP、HLS 格式：FLV、MP4、MPEG-TS、Matroska (MKV)、MP3、wav
        private String token;
        private Long uid; // 用户 ID。
        private Integer idleTimeout; // 云端播放器处于空闲状态的最大时长（秒），不可设置超过 24 小时。 当媒体流为非播放状态的时长超过该设定值时，任务会自动销毁。
        private Integer playTs; // 云端播放器开始播放在线媒体流时的 Unix 时间戳（秒）。

        private Integer mediaType; // 0音频， 1视频， 2音视频
        private Integer volume; //设置音量大小，取值范围为[0,200]，默认值为100，表示播放原始声音，大于100表示增大音量，小于100表示减小音量
        private Integer repeatTime; // 重复播放次数，默认值为1，如果填-1表示无限循环，直到任务停止为止
    }
}
