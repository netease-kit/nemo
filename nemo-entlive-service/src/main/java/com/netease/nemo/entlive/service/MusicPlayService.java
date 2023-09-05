package com.netease.nemo.entlive.service;

import com.netease.nemo.entlive.dto.PlayDetailInfoDto;
import com.netease.nemo.entlive.parameter.MusicActionParam;

/**
 * 歌曲播放Service
 *
 * @Author：CH
 * @Date：2023/5/31 3:59 下午
 */
public interface MusicPlayService {

    /**
     * 获取直播间播放的歌曲信息
     *
     * @param liveRecordId 直播间唯一编号
     * @return PlayDetailInfoDto 歌曲播放详情
     */
    PlayDetailInfoDto getPlayMusicInfo(Long liveRecordId);

    /**
     * 歌曲准备（下载OK）
     *
     * @param liveRecordId 直播间唯一编号
     * @param orderId      点歌编号
     */
    void musicReady(Long liveRecordId, Long orderId);

    /**
     * 歌曲播放操作
     *
     * @param userUuid     操作者编号
     * @param param        操作对象
     */
    void musicAction(String userUuid, MusicActionParam param);

    /**
     * 清空当前直播间歌曲播放信息
     *
     * @param liveRecordId 直播间唯一编号
     * @param orderId      点歌编号
     */
    void cleanPlayerMusicInfo(Long liveRecordId, Long orderId);


    /**
     * 清空当前直播间歌曲播放信息
     * @param liveRecordId 直播间唯一编号
     */
    void cleanPlayerMusicInfo(Long liveRecordId);
}
