package com.netease.nemo.game.service;

/**
 * 游戏上报处理接口
 *
 * @Author：CH
 * @Date：2023/9/7 2:31 PM
 */
public interface GameReportService {

    /**
     * 游戏上报信息
     * @param reportGameInfo 上报信息
     */
    void reportGameInfo(String reportGameInfo);
}
