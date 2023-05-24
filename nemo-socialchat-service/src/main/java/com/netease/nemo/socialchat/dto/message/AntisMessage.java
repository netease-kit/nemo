package com.netease.nemo.socialchat.dto.message;

import com.netease.nemo.socialchat.dto.antis.AudioEvidence;
import com.netease.nemo.socialchat.dto.antis.VideoEvidence;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;
import com.netease.nemo.util.ObjectMapperUtil;
import lombok.Data;

/**
 * 音视频安全通审计内容（发送端上信息）
 */
@Data
public class AntisMessage {
    /**
     * 音视频房间 ID
     **/
    private Long channelId;

    /**
     * 音视频房间名称
     **/
    private String channelName;

    /**
     * 音频证据
     **/
    private AudioEvidence audio;

    /**
     * 视频证据
     **/
    private VideoEvidence video;

    /**
     * 云信自动生成的数据唯一标识，如果您对检测结果有任何疑问，
     * 请根据 dataId 咨询技术支持
     **/
    private String dataId;

    public static AntisMessage build(RtcAntispamDto rtcAntispamDto) {
        if (rtcAntispamDto == null) {
            return null;
        }
        AntisMessage antisMessage = new AntisMessage();
        antisMessage.setChannelId(rtcAntispamDto.getChannelId());
        antisMessage.setChannelName(rtcAntispamDto.getChannelName());
        antisMessage.setDataId(rtcAntispamDto.getDataId());
        RtcAntispamDto.Evidence evidences = rtcAntispamDto.getEvidences();
        if (evidences != null) {
            if (null != evidences.getAudio()) {
                antisMessage.setAudio(ObjectMapperUtil.map(rtcAntispamDto.getEvidences().getAudio(), AudioEvidence.class));
            }
            RtcAntispamDto.Video video = evidences.getVideo();
            if (null != video && null != video.getEvidence()) {
                antisMessage.setVideo(ObjectMapperUtil.map(video.getEvidence(), VideoEvidence.class));
            }
        }
        return antisMessage;
    }

}
