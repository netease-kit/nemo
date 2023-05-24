package com.netease.nemo.openApi.dto.antispam;

import com.google.gson.JsonObject;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class RtcAntispamDto {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_AUDIO_VIDEO = 3;

    String data;
    /**
     * channelId
     */
    private Long channelId;
    /**
     * channelName
     */
    private String channelName;
    /**
     * 实时音视频安全通审核ID，是其唯一标识。
     */
    private String taskId;
    /**
     * 调用创建安全通审核接口时，传递的 callback 字段数据。
     */
    private String callback;
    /**
     * 网易云信为您自动生成的数据唯一标识，如果您对检测结果有任何疑问，请根据 dataId 咨询技术支持。
     */
    private String dataId;
    /**
     * 检测状态。101：检测中。102：检测结束。
     */
    private Integer status;

    /**
     * 审核来源，0：易盾人审，1：客户人审，2：易盾机审
     */
    private Integer censorSource;
    /**
     * evidences
     */
    private Evidence evidences;
    /**
     * reviewEvidences
     */
    private Review reviewEvidences;

    public static RtcAntispamDto build(JsonObject data) {
        RtcAntispamDto rtcAntispamDto = GsonUtil.fromJson(data, RtcAntispamDto.class);
        rtcAntispamDto.data = data.toString();
        return rtcAntispamDto;
    }

    public RtcAntispam toRtcAntispam() {
        RtcAntispam rtcAntispam = new RtcAntispam();
        rtcAntispam.setChannelId(this.channelId);
        rtcAntispam.setChannelName(this.channelName);
        rtcAntispam.setData(this.data);
        rtcAntispam.setDataId(this.getDataId());
        rtcAntispam.setTaskId(this.taskId);
        rtcAntispam.setStatus(this.status);
        Evidence evidence = this.evidences;
        if (evidence == null) {
            Review review = this.getReviewEvidences();
            if (review == null
                    || review.evidence == null //没有证据
                    || review.evidence.size() == 0 //没有证据
                    || Objects.equals(review.label, 0) //不违规
            ) {
                return null;
            } else {
                rtcAntispam.setType(TYPE_NONE);
            }
        } else {
            boolean isAudio = isAudio(evidence.audio);
            boolean isVideo = isVideo(evidence.video);

            if (isAudio && isVideo) {
                rtcAntispam.setType(TYPE_AUDIO_VIDEO);
            } else if (isVideo) {
                rtcAntispam.setType(TYPE_VIDEO);
            } else if (isAudio) {
                rtcAntispam.setType(TYPE_AUDIO);
            } else {
                return null;
            }
        }
        return rtcAntispam;
    }

    private static boolean isAudio(Audio audio) {
        if (audio == null) {
            return false;
        }
        if (!Objects.equals(audio.action, 1) && //嫌疑
                !Objects.equals(audio.action, 2)) { //不通过
            return false;
        }
        if (audio.uid == null || Objects.equals(audio.uid, 0L)) {
            return false;
        }
        return true;
    }

    private static boolean isVideo(Video video) {
        if (video == null) {
            return false;
        }
        if (video.evidence == null && video.labels == null) {
            return false;
        }
        return true;
    }

    @Data
    public static class Evidence {
        private Audio audio;
        private Video video;
    }

    @Data
    public static class Audio {
        private Integer action;
        private Integer asrStatus;
        private Integer asrResult;
        private Long uid;
        private Long startTime;
        private Long endTime;
        private String content;
    }

    @Data
    public static class AudioSegment {
        private Integer label;
        private Integer level;
        private List<AudioSegmentsSubLabel> subLabels;
    }

    @Data
    public static class AudioSegmentsSubLabel {
        private Map<String, Object> details;
        private String subLabel;
    }

    @Data
    public static class Video {
        private VideoEvidence evidence;
        private List<VideoLabel> labels;
    }

    @Data
    public static class VideoEvidence {
        private Long beginTime;
        private Long endTime;
        private Integer type;
        private String url;
        private Long uid;
    }

    @Data
    public static class VideoLabel {
        /**
         * 100：色情，110：性感；200：广告，210：二维码，260：广告法，300：暴恐，400：违禁，500：涉政，800：恶心类，900：其他，1020：黑屏，1030：挂机，1100：涉价值观。
         */
        private Integer label;
        /**
         * 级别信息。1：不确定，2：确定。
         */
        private Integer level;

        /**
         * 分数。
         */
        private Double rate;
    }

    @Data
    public static class VideoSubLabel {
        private Integer subLabel;
        /**
         * 置信度分数，0-1之间取值，1为置信度最高，0为置信度最低。
         */
        private Double rate;
    }

    @Data
    public static class VideoSubLabelDetail {
        private String[] hitInfos;
        private List<VideoSubLabelDetailImageTagInfo> imageTagInfo;
        private List<VideoSubLabelDetailHitLocationInfo> hitLocationInfos;
    }

    @Data
    public static class VideoSubLabelDetailImageTagInfo {
        private String tagName;
        private String tagGroup;
    }

    @Data
    public static class VideoSubLabelDetailHitLocationInfo {
        private String hitInfo;
        private Integer x1;
        private Integer y1;
        private Integer x2;
        private Integer y2;
    }

    @Data
    public static class Review {
        /**
         * 审核操作。1为忽略，2为警告，3为断流，4为提示，10为机器检测结束。
         */
        private Integer action;
        /**
         * 操作时间，UNIX_TIME时间戳，单位为毫秒。
         */
        private Long actionTime;
        /**
         * 其中，0 表示不违规。
         * 100-色情，111-淫秽表演，112-偶然走光，113-服装暴露，114-床上直播，115-音乐内容低俗，116-言论低俗涉黄，117-违规服装，118-上装暴露，119-下装暴露，120-低俗连麦，涉黄200-广告，210-二维码，211-商业推广，212-提及竞品，213-字幕推广，300-暴恐，311恐怖主义，312-暴力血腥，400-违禁，411-危险表演，412-涉毒/赌/传销，413-违禁服装，500-涉政，511-影响政府形象，512-邪教迷信，513-涉军事，514涉宗教，800-不文明，811-赤膊，812-性感，821-涉酒，822-涉烟，823-纹身，824-未成年，825-危险驾驶，826-传播负面情绪，1000-其他，1020-黑屏，1022-画质差，1023-无营养，1024-盗播版权内容，1025-无音乐设备，1026-与音乐无关，1030-挂机，1050-自定义。
         */
        private Integer label;
        private String detail;
        private Integer warnCount;
        private List<ReviewEvidence> evidence;
    }

    @Data
    public static class ReviewEvidence {
        private String snapshot;
    }
}
