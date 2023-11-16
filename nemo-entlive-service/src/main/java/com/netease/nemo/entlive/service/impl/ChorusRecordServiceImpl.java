package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.entlive.enums.ChorusStateEnum;
import com.netease.nemo.entlive.enums.StatusEnum;
import com.netease.nemo.entlive.model.po.ChorusRecord;
import com.netease.nemo.entlive.service.ChorusRecordService;
import com.netease.nemo.entlive.wrapper.ChorusRecordMapperWrapper;
import com.netease.nemo.exception.BsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * ChorusRecord记录服务
 *
 * @Author：CH
 * @Date：2023/10/16 10:24 AM
 */
@Service
@Slf4j
public class ChorusRecordServiceImpl implements ChorusRecordService {

    @Resource
    private ChorusRecordMapperWrapper chorusRecordMapperWrapper;


    @Override
    public void addChorusRecord(ChorusRecord chorusRecord) {
        chorusRecordMapperWrapper.insertSelective(chorusRecord);
    }

    @Override
    public ChorusRecord getChorusRecordByChorusId(String chorusId) {
        ChorusRecord chorusRecord = chorusRecordMapperWrapper.selectByChorusId(chorusId);
        if (null == chorusRecord || chorusRecord.getStatus() == StatusEnum.INVALID.getCode()) {
            throw new BsException(ErrorCode.CHORUS_RECORD_NOT_EXIST);
        }

        return chorusRecord;
    }

    @Override
    public int updateChorusRecord(ChorusRecord chorusRecord) {
        return chorusRecordMapperWrapper.updateByPrimaryKey(chorusRecord);
    }

    @Override
    public ChorusRecord updateChorusState(ChorusRecord chorusRecord, int chorusState) {
        if (chorusRecord == null) {
            return null;
        }

        if (ChorusStateEnum.CHORUS_END.getCode() == chorusState
                || ChorusStateEnum.CHORUS_CANCEL.getCode() == chorusState
                || ChorusStateEnum.CHORUS_REJECT.getCode() == chorusState) {
            chorusRecord.setStatus(StatusEnum.INVALID.getCode());
        }

        chorusRecord.setState(chorusState);

        chorusRecordMapperWrapper.updateByPrimaryKeySelective(chorusRecord);

        return chorusRecord;
    }

    @Override
    public List<ChorusRecord> getNotEndChorusRecord(String roomUuid) {
        if(StringUtils.isEmpty(roomUuid)){
            return Collections.emptyList();
        }
        return chorusRecordMapperWrapper.selectNotEndChorusRecord(roomUuid);
    }

    @Override
    public ChorusRecord getByChorusId(String chorusId) {
        return chorusRecordMapperWrapper.selectByChorusId(chorusId);
    }

    @Override
    public boolean isAssistant(String roomUuid, Long orderId, String userUuid) {
        List<ChorusRecord> chorusRecords = chorusRecordMapperWrapper.selectOrderSongChorusRecord(roomUuid, orderId);
        if(chorusRecords == null || chorusRecords.isEmpty()){
            return false;
        }
        for (ChorusRecord chorusRecord : chorusRecords) {
            String assistantUuid = chorusRecord.getAssistantUuid();
            if(StringUtils.isEmpty(assistantUuid)){
                continue;
            }
            if(assistantUuid.equals(userUuid)){
                return true;
            }
        }
        return false;
    }
}
