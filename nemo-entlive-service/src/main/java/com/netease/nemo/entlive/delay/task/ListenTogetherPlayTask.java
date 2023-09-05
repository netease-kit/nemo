package com.netease.nemo.entlive.delay.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一起听歌歌曲开始播放延迟任务
 *
 * @Author：CH
 * @Date：2023/8/9 10:59 AM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListenTogetherPlayTask {
    private Long liveRecordId;
    private Long orderId;
}
