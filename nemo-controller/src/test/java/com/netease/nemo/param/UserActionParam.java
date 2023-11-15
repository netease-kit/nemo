package com.netease.nemo.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserActionParam {

    /**
     * 麦序
     */
    private Integer seatIndex;

    /**
     * 麦位操作action
     */
    private Integer action;

    /**
     * 一般是：观众申请上麦、管理员抱麦时，传了seatIndex的前提下，再设置此字段
     * 是否要锁麦位。true:锁住，false:不锁。默认不锁
     */
    private boolean lockIndex;

    /**
     * 用户申请上麦时的扩展字段
     */
    @Length(message = "{EntryRoomParam.ext.Length}", max = 2048)
    private String ext;
}
