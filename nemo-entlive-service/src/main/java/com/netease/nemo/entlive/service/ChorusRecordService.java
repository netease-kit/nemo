package com.netease.nemo.entlive.service;


import com.netease.nemo.entlive.model.po.ChorusRecord;

import java.util.List;

public interface ChorusRecordService {

    /**
     * 添加合唱记录
     *
     * @param chorusRecord chorusRecord
     * @return ChorusRecord
     */
    void addChorusRecord(ChorusRecord chorusRecord);


    /**
     * 获取合唱记录
     *
     * @param chorusId chorusId
     * @return ChorusRecord
     */
    ChorusRecord getChorusRecordByChorusId(String chorusId);

    /**
     * 更新合唱记录
     *
     * @param chorusRecord chorusRecord
     * @return int
     */
    int updateChorusRecord(ChorusRecord chorusRecord);

    /**
     * 合唱状态更新
     *
     * @param chorusRecord 合唱记录
     * @param chorusState  合唱状态
     * @return ChorusRecord
     */
    ChorusRecord updateChorusState(ChorusRecord chorusRecord, int chorusState);

    /**
     * 获取房间内未结束的合唱记录
     *
     * @param roomUuid roomUuid
     * @return List<ChorusRecord>
     **/
    List<ChorusRecord> getNotEndChorusRecord(String roomUuid);

    /**
     * 获取合唱记录
     *
     * @param chorusId chorusId
     * @return ChorusRecord
     */
    ChorusRecord getByChorusId(String chorusId);

    /**
     * 判断用户是否为副唱
     *
     * @param roomUuid roomUuid
     * @param orderId  orderId
     * @param userUuid userUuid
     **/
    boolean isAssistant(String roomUuid, Long orderId, String userUuid);
}
