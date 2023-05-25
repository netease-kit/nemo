package com.netease.nemo.socialchat.parameter.rtcNotify;

import lombok.Data;

@Data
public class AntisResultNotifyParam {
    Long channelId; // 进行内容审核的音视频房间 ID。
    String channelName; // 进行内容审核的音视频房间名称。
    String taskId; // 实时音视频安全通审核ID，是其唯一标识
    String callback; // 调用创建安全通审核接口时，传递的 callback 字段数据。
    String dataId; // 网易云信为您自动生成的数据唯一标识，如果您对检测结果有任何疑问，请根据 dataId 咨询技术支持。
    Integer status; // 检测状态。101：检测中。102：检测结束。
    Integer censorSource; //审核来源，0：易盾人审，1：客户人审，2：易盾机审
    Object evidences; // 机审证据信息。
    Object reviewEvidences; // 人审证据信息。
}
