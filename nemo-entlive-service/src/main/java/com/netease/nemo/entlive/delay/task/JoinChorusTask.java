package com.netease.nemo.entlive.delay.task;


import com.netease.nemo.entlive.model.po.ChorusRecord;
import lombok.Data;

@Data
public class JoinChorusTask {

    /**
     * 房间编号
     */
    private String roomUuid;

    /**
     * 合唱 编号
     */
    private String chorusId;

    /**
     * 点歌编号
     */
    private Long orderId;

    public JoinChorusTask() {
    }

    public JoinChorusTask(ChorusRecord chorusRecord) {
        this.roomUuid = chorusRecord.getRoomUuid();
        this.chorusId = chorusRecord.getChorusId();
        this.orderId = chorusRecord.getOrderId();
    }
}
